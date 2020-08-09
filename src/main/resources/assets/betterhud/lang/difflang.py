#!/usr/bin/env python3
import json, math, sys
from pathlib import Path

USAGE = f"""
Usage: {__file__} FILE...

For each missing key in any FILE, prints the first corresponding pair from the
others. The FILEs are searched in the order specified.

For example, if you know both English and Russian:
{__file__} en_us.json ru_ru.json
""".strip()

def dict_minus(a, b):
  return {key: a[key] for key in a if key not in b}

def format_percent(x):
  # Use floor to prevent 99.8% rounding to 100%
  return f"{math.floor(x * 100)}%"

if len(sys.argv) <= 1:
  print(USAGE)
else:
  langs = {}
  completion = {}
  defaults = {}

  for file in reversed(sys.argv[1:]):
    with open(file, encoding="utf-8") as fp:
      lang = json.load(fp)

      langs[file] = lang
      defaults.update(lang)

  for file in langs:
    completion[file] = len(langs[file]) / len(defaults)

    missing = dict_minus(defaults, langs[file])

    print(f"Missing from {file}:", json.dumps(missing, indent=2))

  print("Summary:")
  col_width = max(len(file) for file in langs)
  for file in langs:
    print(file.ljust(col_width), format_percent(completion[file]), sep="  ")
