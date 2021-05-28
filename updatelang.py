#!/usr/bin/env python3
import argparse, glob, json, os, sys

LANG_DIR = "src/main/resources/assets/betterhud/lang/"
JSON_ARGS = {
  "indent": 2,
  "sort_keys": True,
  "ensure_ascii": False
}

def language_file(language_code):
  return f"{LANG_DIR}{language_code}.json"

parser = argparse.ArgumentParser(description="Creates or adds missing entries to a language file.")
parser.add_argument("target", help="target language code")
parser.add_argument("source", nargs="*", help="source language codes in order of preference (default: en_us)", default=["en_us"])
parser.add_argument("--all-sources", action="store_true", help="add all languages to the end of source")
args = parser.parse_args()

if args.all_sources:
  for file in glob.iglob(f"{LANG_DIR}*.json"):
    name, _ = os.path.splitext(os.path.basename(file))
    args.source.append(name)

args.source = list(dict.fromkeys(filter(lambda source: source != args.target, args.source)))
if not args.source:
  print("Error: no source language or source language is target language", file=sys.stderr)
  exit(1)

print("Updating", args.target, "from", ", ".join(args.source))

source_entries = {}
for language_code in reversed(args.source):
  with open(language_file(language_code), "r", encoding="utf-8") as fp:
    source_entries.update(json.load(fp))

target_entries = {}
target_file = language_file(args.target)

if os.path.isfile(target_file):
  with open(target_file, "r", encoding="utf-8") as fp:
    target_entries = json.load(fp)

missing_entries = {
  key: source_entries[key]
  for key in source_entries
  if key not in target_entries
}

if missing_entries:
  target_entries.update(missing_entries)

  with open(target_file, "w", encoding="utf-8") as fp:
    json.dump(target_entries, fp, **JSON_ARGS)
    fp.write("\n")

  print("Added entries to", args.target)
  json.dump(missing_entries, fp=sys.stdout, **JSON_ARGS)
  print()
else:
  print("No missing entries")
