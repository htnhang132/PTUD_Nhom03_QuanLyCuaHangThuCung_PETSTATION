import re

with open('database/002_sample_data.sql', 'r', encoding='utf-8') as f:
    text = f.read()

# Helper mappings
def nv_code(nid):
    # 1 -> NV0001, 8 -> NV0002, 9 -> NV0003, ..., 17 -> NV0011
    if nid == 1:
        return 'NV0001'
    return f'NV{nid-6:04d}'

def tk_username(tid):
    m = {1: 'admin', 2: 'demo.admin', 3: 'demo.sales', 4: 'demo.stock', 5: 'demo.spa'}
    return m.get(tid, f'user_{tid}')

def kh_code(kid):
    if kid is None:
        return 'NULL'
    # 7..35 -> KH000001..KH000029
    return f'KH{kid-6:06d}'

def sp_code(spid):
    if spid is None:
        return 'NULL'
    # 17..76 -> SP000015..SP000074
    return f'SP{spid-2:06d}'

def lo_code(lid):
    if lid is None:
        return 'NULL'
    # 17..76 -> LO00000016..LO00000075
    return f'LO{lid-1:08d}'

def ph_code(pid):
    # 1..17 -> PH00000001..PH00000017
    return f'PH{pid:08d}'

def goi_code(gid):
    # 1..10 -> SPA00000001..SPA00000010
    return f'SPA{gid:08d}'

def llv_code(lid):
    # 1..32 -> LLV000001..LLV000032
    return f'LLV{lid:06d}'

def phi_code(pid):
    if pid is None:
        return 'NULL'
    # 1..33 -> MPI00000001..MPI00000033
    return f'MPI{pid:08d}'

def hd_code(hid):
    if hid is None:
        return 'NULL'
    if hid <= 34:
        return f'HD261019{hid:04d}'
    else:
        return f'TREO{hid-34:06d}'

def tc_code(tcid):
    if tcid is None:
        return 'NULL'
    # 6..42 -> TC000001..TC000037
    return f'TC{tcid-5:06d}'

def hdht_code(hid):
    if hid is None:
        return 'NULL'
    return f'HDHT{hid:06d}'

def lh_code(lhid):
    if lhid is None:
        return 'NULL'
    return f'LH{lhid:06d}'

def cthd_code(cid):
    return f'CTHD{cid:06d}'

def pn_code(pid):
    return f'PN{pid:08d}'

def px_code(pid):
    return f'PX{pid:08d}'

def dt_code(did):
    return f'DT{did:08d}'

print("Mappings loaded.")

