import csv
from pathlib import Path
csv_path = Path('Vulnerability Test Results') / \
    'Skin_Journey_QA_Test_Cases.csv'
rows = []
with csv_path.open(newline='', encoding='utf-8') as f:
    reader = csv.DictReader(f)
    for row in reader:
        rows.append(row)
count = len(rows)
passed = sum(1 for r in rows if r['Pass/Fail'].strip().lower() == 'pass')
failed = sum(1 for r in rows if r['Pass/Fail'].strip().lower() == 'fail')
blank = sum(1 for r in rows if not r['Pass/Fail'].strip())
print(f'{count} total')
print(f'{passed} passed')
print(f'{failed} failed')
print(f'{blank} untested')
for i, r in enumerate(rows, 1):
    val = r['Pass/Fail']
    if val.strip() and val.strip().lower() not in ('pass', 'fail'):
        print(f"{i}: {r['Test Case ID']} - {repr(val)}")
