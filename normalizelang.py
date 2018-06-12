#!/usr/bin/env python3
import os, sys

ROOT_DIR = "src/main/resources/assets/hud/lang"
CANONICAL = "en_us.lang"

with open(os.path.join(ROOT_DIR, "en_US.lang")) as f:
    lines = f.readlines()

for f in os.listdir(ROOT_DIR):
    if f.lower() == CANONICAL: continue

    mapping = {}
    with open(os.path.join(ROOT_DIR, f), "r", encoding="utf-8") as f1:
        for line in f1.readlines():
            if line[0] != "#":
                kv = line.strip().split("=", 1)
                if len(kv) == 2: mapping[kv[0]] = kv[1]

    with open(os.path.join(ROOT_DIR, f), "w", encoding="utf-8") as f1:
        untranslated = 0

        for line in lines:
            if not line.strip() or line[0] == "#":
                f1.write(line)
            else:
                key = line.split("=", 1)[0]

                if key in mapping:
                    f1.write(key + "=" + mapping[key] + "\n")
                    del mapping[key]
                else:
                    untranslated += 1
                    f1.write("#" + line)

        if untranslated > 0:
            print("Found", untranslated, "untranslated lines in", f)

        if mapping:
            print("Found", len(mapping), "unmapped lines in", f)
            f1.write("\n# Unmapped lines\n")

            for key in mapping:
                line = key + "=" + mapping[key]
                f1.write(line + "\n")
