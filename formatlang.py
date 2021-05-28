#!/usr/bin/env python3
import glob, json, os

for file in glob.iglob("src/main/resources/assets/betterhud/lang/*.json"):
  file = os.path.normpath(file)

  json_object = None
  with open(file, "r", encoding="utf-8") as fp:
    json_object = json.load(fp)

  with open(file, "w", encoding="utf-8") as fp:
    json.dump(json_object, fp, indent=2, sort_keys=True, ensure_ascii=False)
    fp.write("\n")

  print(f"Formatted {file}")
