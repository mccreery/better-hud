#!/usr/bin/env python3
import os

MODID = "betterhud"
ROOT_DIR = os.path.join("src", "main", "resources", "assets", MODID, "lang")
CANONICAL = "en_US.lang"

HEADER = ("file", "score", "changed", "unchanged", "missing", "extra")
STORE_FROM = 1

def get(line):
    line = line.strip()

    if "=" in line and line[0] != "#":
        return line.split("=", 1)
    else:
        return None

def get_row(silent, *cells, column_width = 10):
    if not silent:
        print(" ".join(str(cell).ljust(column_width) for cell in cells))

    return {HEADER[i]: cells[i] for i in range(STORE_FROM, min(len(cells), len(HEADER)))}

def get_data(silent = False):
    data = {}

    with open(os.path.join(ROOT_DIR, CANONICAL), encoding="utf-8") as f:
        lines = f.readlines()

    get_row(silent, *HEADER)

    entries = sum(bool(get(l)) for l in lines)
    data[CANONICAL] = get_row(silent, CANONICAL, "100%", 0, entries, 0, 0)

    scores = []

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
            identical = 0

            for line in lines:
                kv = get(line)
                if kv:
                    key = kv[0]

                    if key in mapping:
                        if mapping[key] == kv[1]:
                            identical += 1
                        else:
                            translated += 1

                        f1.write(key + "=" + mapping[key] + "\n")
                        del mapping[key]
                    else:
                        untranslated += 1
                        f1.write("#" + line)
                else:
                    f1.write(line)

            score = (translated + identical) / entries * 100
            scores.append(score)

            data[f] = get_row(silent, f, str(round(score)) + "%", translated, identical, untranslated, len(mapping))

            if mapping:
                f1.write("\n# Unmapped lines\n")

                for key in mapping:
                    line = key + "=" + mapping[key]
                    f1.write(line + "\n")

    data["total"] = get_row(silent, "total", str(round(sum(scores) / len(scores))) + "%")
    return data

if __name__ == "__main__":
    import sys
    isjson = "-j" in sys.argv or "--json" in sys.argv

    data = get_data(isjson)

    if isjson:
        import json
        print(json.dumps(data))
