#!/usr/bin/env python3
import os, sys

ROOT_DIR = "src/main/resources/assets/hud/lang"
CANONICAL = "en_US.lang"

def print_row(column_width, *cells):
    print(*(str(cell).ljust(column_width) for cell in cells))

def get(line):
    line = line.strip()

    if "=" in line and line[0] != "#":
        return line.split("=", 1)
    else:
        return None

with open(os.path.join(ROOT_DIR, CANONICAL)) as f:
    lines = f.readlines()

print_row(12, "file", "", "translated", "untranslated", "unmapped")

entries = sum(bool(get(l)) for l in lines)
print_row(12, CANONICAL, "100%", entries, 0, 0)

for f in os.listdir(ROOT_DIR):
    if f.lower() == CANONICAL.lower(): continue

    mapping = {}
    with open(os.path.join(ROOT_DIR, f), "r", encoding="utf-8") as f1:
        for line in f1.readlines():
            kv = get(line)
            if kv: mapping[kv[0]] = kv[1]

    with open(os.path.join(ROOT_DIR, f), "w", encoding="utf-8") as f1:
        untranslated = 0
        translated = 0

        for line in lines:
            kv = get(line)
            if kv:
                key = kv[0]

                if key in mapping:
                    translated += 1
                    f1.write(key + "=" + mapping[key] + "\n")
                    del mapping[key]
                else:
                    untranslated += 1
                    f1.write("#" + line)
            else:
                f1.write(line)

        score = round(translated / (untranslated + translated) * 100)
        print_row(12, f, str(score) + "%", translated, untranslated, len(mapping))

        if mapping:
            f1.write("\n# Unmapped lines\n")

            for key in mapping:
                line = key + "=" + mapping[key]
                f1.write(line + "\n")
