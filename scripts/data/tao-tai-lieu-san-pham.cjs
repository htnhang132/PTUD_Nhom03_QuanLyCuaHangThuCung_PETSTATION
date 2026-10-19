const fs=require('node:fs');
const path=require('node:path');
const root=path.resolve(__dirname,'../..');
const ds=JSON.parse(fs.readFileSync(path.join(root,'docs/data/san-pham-nguon.json'),'utf8'));
const esc=s=>String(s).replaceAll('|','/');
let noiDung=`# Nguồn sản phẩm và ảnh

Bộ REAL_DATA_V1 gồm 60 mã hàng công khai từ Pet Mart, thu thập ngày ${ds[0].ngayThuThap}. Giá VND gắn với biến thể/quy cách ghi bên dưới, không dùng giá tối thiểu của sản phẩm cha. Mô tả trong ứng dụng được viết lại ngắn gọn; ảnh giữ nội dung gốc và ghi nguồn.

Bảng máy đọc, mã biến thể, URL ảnh gốc và SHA-256: [san-pham-nguon.json](data/san-pham-nguon.json). Trang nguồn công bố yêu cầu dẫn nguồn tại [Pet Mart](https://www.petmart.vn/thuc-an-cho-cho-truong-thanh-co-nho-royal-canin-mini-adult).

| Mã | Sản phẩm / quy cách | Giá tham khảo | Nguồn | Ảnh cục bộ |
| --- | --- | ---: | --- | --- |
`;
for(const p of ds)noiDung+=`| ${p.ma} | ${esc(p.ten)} | ${p.gia.toLocaleString('vi-VN')} | [Sản phẩm](${p.nguon}) | [Ảnh](../src/main/resources/${p.anh}) |\n`;
noiDung+=`
Sản phẩm cũ tên chung như vòng cổ size M, bóng cao su hoặc Royal Canin Adult thiếu dòng cụ thể chưa được gán ảnh tùy ý. Giữ mã, giá và lịch sử cũ; giao diện hiển thị “Chưa có ảnh” đến khi xác minh đúng sản phẩm.

Quy cách chưa được nguồn xác minh để trống. Không tự bổ sung kích thước, dung tích hoặc barcode. Số lượng nhập, giá nhập, hạn dùng, hồ sơ cá nhân và giao dịch bổ sung là dữ liệu mẫu; nhãn kỹ thuật và nguồn được lưu riêng, không đưa vào nội dung nghiệp vụ trên giao diện.
`;
fs.writeFileSync(path.join(root,'docs/NGUON_SAN_PHAM.md'),noiDung);
console.log('Generated source table for 60 products.');
