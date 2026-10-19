/** Thu thập sản phẩm công khai; không dùng khoảng giá của sản phẩm cha cho biến thể. */
const fs = require('node:fs/promises');
const path = require('node:path');
const crypto = require('node:crypto');
const root = path.resolve(__dirname,'../..');
const base = 'https://www.petmart.vn';
const ngay = new Intl.DateTimeFormat('en-CA', {timeZone:'Asia/Ho_Chi_Minh'}).format(new Date());
const giaiMa = s => String(s || '').replace(/&#(\d+);/g, (_, n) => String.fromCodePoint(+n)).replace(/&quot;/g,'"').replace(/&#x([\da-f]+);/gi,(_,n)=>String.fromCodePoint(parseInt(n,16))).replace(/&amp;/g,'&').replace(/&lt;/g,'<').replace(/&gt;/g,'>');
async function tai(url, json=false) {
  for(let i=0;i<3;i++) {
    try { const r=await fetch(url,{signal:AbortSignal.timeout(30000)}); if(!r.ok)throw new Error(`HTTP ${r.status}`); return json?await r.json():r; }
    catch(e) { if(i===2)throw e; await new Promise(r=>setTimeout(r,700)); }
  }
}
function kichThuoc(b) {
  if(b.subarray(1,4).toString()==='PNG')return [b.readUInt32BE(16),b.readUInt32BE(20),'png'];
  if(b[0]!==255||b[1]!==216)return null;
  let i=2;
  while(i+9<b.length){if(b[i]!==255){i++;continue;}const m=b[i+1];i+=2;if(m===216||m===217)continue;const len=b.readUInt16BE(i);if([192,193,194,195,197,198,199,201,202,203,205,206,207].includes(m))return [b.readUInt16BE(i+5),b.readUInt16BE(i+3),'jpg'];if(len<2)break;i+=len;}
  return null;
}
function phanNhom(p) {
  const ten=giaiMa(p.name).toLowerCase();
  const cats=p.categories.map(c=>giaiMa(c.name).toLowerCase()).join(' ');
  if(/trị|điều trị|thuốc|nấm|ve rận|dầu cá|vitamin|bổ sung/.test(ten))return null;
  if(/thức ăn|bánh thưởng|pate|súp thưởng|xương.*gặm|snack/.test(cats+' '+ten))return 'Thức ăn';
  if(/sữa tắm|dầu gội|lược|bàn chải lông|cắt móng|tỉa|groom/.test(cats+' '+ten))return 'Grooming';
  if(/cát mèo|cát vệ sinh|vệ sinh|khử mùi|tã|bỉm|khay|xẻng|khăn ướt/.test(cats+' '+ten))return 'Vệ sinh';
  if(/vòng cổ|dây dắt|đồ chơi|bát|balo|túi vận|lồng|nhà|đệm|quần áo|áo|yếm/.test(cats+' '+ten))return 'Phụ kiện';
  return null;
}
async function main(){
  // Bộ đã nhập là bất biến: lần chạy sau chỉ khôi phục ảnh thiếu theo chính bảng nguồn cũ.
  const manifest=path.join(root,'docs/data/san-pham-nguon.json');
  try {
    const daCo=JSON.parse(await fs.readFile(manifest,'utf8'));
    if(daCo.length!==60)throw new Error('Bảng nguồn hiện có chưa đủ 60 sản phẩm; cần đối chiếu trước.');
    for(const p of daCo) {
      const dich=path.join(root,'src/main/resources',p.anh);let bytes;
      try { bytes=await fs.readFile(dich); } catch { bytes=Buffer.from(await(await tai(p.nguonAnh)).arrayBuffer()); }
      if(crypto.createHash('sha256').update(bytes).digest('hex')!==p.sha256)throw new Error(`Ảnh nguồn đã thay đổi: ${p.ma}; không tự ghi đè bộ đã nhập.`);
      await fs.mkdir(path.dirname(dich),{recursive:true});
      try { await fs.access(dich); } catch { await fs.writeFile(dich,bytes); }
    }
    console.log('Verified/restored 60 existing images; source mapping and prices unchanged.');return;
  }catch(e){if(e.code!=='ENOENT')throw e;}
  const cache=path.join(root,'target','product-import');await fs.mkdir(cache,{recursive:true});
  let products=[];
  for(let page=1;page<=4;page++) {
    const file=path.join(cache,`catalog-${page}.json`);let a;
    try{a=JSON.parse(await fs.readFile(file,'utf8'));}catch{a=await tai(`${base}/wp-json/wc/store/v1/products?per_page=100&orderby=popularity&page=${page}`,true);await fs.writeFile(file,JSON.stringify(a));}
    products.push(...a);
  }
  const quotas={'Thức ăn':24,'Phụ kiện':16,'Grooming':10,'Vệ sinh':10};
  const prefixes={'Thức ăn':'TA','Phụ kiện':'PK','Grooming':'GR','Vệ sinh':'VS'};
  const counts=Object.fromEntries(Object.keys(quotas).map(k=>[k,0]));const result=[];
  const dir=path.join(root,'src/main/resources/images/products');await fs.mkdir(dir,{recursive:true});
  for(const p of products){
    const group=phanNhom(p);if(!group||counts[group]>=quotas[group]||!p.brands?.length||!p.images?.length)continue;
    try {
      let price=Number(p.prices.price), image=p.images[0].src, variationId=null;
      const attributes=[];let variation=null;
      if(p.type==='variable'){
        const file=path.join(cache,`${p.id}.html`);let html;try{html=await fs.readFile(file,'utf8');}catch{html=await(await tai(p.permalink)).text();await fs.writeFile(file,html);}
        const m=html.match(/data-product_variations="([^"]+)"/);if(!m)continue;
        const variants=JSON.parse(giaiMa(m[1]));
        variation=variants.find(v=>v.variation_is_active&&v.variation_is_visible&&v.display_price>0&&Object.values(v.attributes).every(Boolean));
        if(!variation)continue;
        price=variation.display_price;image=variation.image?.full_src||variation.image?.url||image;variationId=variation.variation_id;
      }else if(p.type!=='simple')continue;
      if(!(price>0)||p.prices.currency_code!=='VND'||p.prices.currency_minor_unit!==0)continue;
      for(const a of p.attributes){
        if(/xuat-xu|mau-sac/.test(a.taxonomy))continue;
        const slug=variation?.attributes[`attribute_${a.taxonomy}`];
        const term=slug?a.terms.find(t=>t.slug===slug):(!a.has_variations&&a.terms.length===1?a.terms[0]:null);
        if(term)attributes.push(`${giaiMa(a.name).replace(/\s*\([^)]*\)/g,'')}: ${giaiMa(term.name)}`);
      }
      const bytes=Buffer.from(await(await tai(image)).arrayBuffer());const size=kichThuoc(bytes);
      if(!size||Math.max(size[0],size[1])<600)continue;
      const code=`SP-${prefixes[group]}-${String(counts[group]+1).padStart(3,'0')}`;
      const relative=`images/products/${code}.${size[2]}`;await fs.writeFile(path.join(root,'src/main/resources',relative),bytes);
      const spec=attributes.join('; ')||'1 sản phẩm theo mẫu của nhà sản xuất';
      const name=giaiMa(p.name);const brand=giaiMa(p.brands.map(x=>x.name).join(', '));
      const object=/chó.*mèo|mèo.*chó/.test(name.toLowerCase())?'Chó và mèo':/mèo/.test(name.toLowerCase())?'Mèo':/chó/.test(name.toLowerCase())?'Chó':'Thú cưng';
      result.push({ma:code,ten:`${name} — ${spec}`.slice(0,180),nhom:group,donVi:/sữa tắm|dầu gội/.test(name.toLowerCase())?'Chai':group==='Thức ăn'||/cát/.test(name.toLowerCase())?'Gói':'Cái',gia:price,thuongHieu:brand,quyCach:spec,doiTuong:object,moTa:`${group} thương hiệu ${brand}, dành cho ${object.toLowerCase()}. Quy cách: ${spec}. Thông tin quy cách và giá được đối chiếu tại trang nguồn; kiểm tra nhãn trước khi sử dụng.`,anh:relative,nguon:p.permalink,nguonAnh:image,ngayThuThap:ngay,idNguon:p.id,idBienThe:variationId,rong:size[0],cao:size[1],sha256:crypto.createHash('sha256').update(bytes).digest('hex')});
      counts[group]++;console.log(`${code}: ${name} / ${price} VND / ${size[0]}x${size[1]}`);
      if(result.length===60)break;
    }catch(e){console.error(`Skip ${p.id}: ${e.message}`);}
  }
  await fs.mkdir(path.join(root,'docs/data'),{recursive:true});
  await fs.writeFile(path.join(root,'docs/data/san-pham-nguon.json'),JSON.stringify(result,null,2)+'\n');
  console.log(JSON.stringify(counts));if(result.length!==60)throw new Error(`Chỉ lấy được ${result.length}/60 sản phẩm`);
}
main().catch(e=>{console.error(e.message);process.exitCode=1;});
