import tabula as tb
import sys
import pandas as pd

try:
    dataFileName = sys.argv[1]
except:
    dataFileName = ""

df = tb.read_pdf(dataFileName, pages = 'all')

pd.set_option("display.max_rows", None, "display.max_columns", None)
pd.set_option("expand_frame_repr", False)

for a in df:
    if a.shape[0] != 0:
        print("DIMENSIONS*** OF** TABLE*", a.shape[0], a.shape[1])
        print(a)