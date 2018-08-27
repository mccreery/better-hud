# Contributing

## Adding Translations

Find the locale code for your language [here](https://minecraft.gamepedia.com/Language#Available_languages)

### Using a Pull Request (preferred):
- Fork the repo
- Create a file (if it doesn't exist) in [`src/main/resources/assets/hud/lang/`](../src/main/resources/assets/hud/lang) called `<locale>.lang`
- Run the Python script [`normalizelang.py`](../normalizelang.py)
- Reopen or reload `<locale>.lang` to see the list of untranslated lines
- Translate as described below
- Re-run [`normalizelang.py`](../normalizelang.py) to see translation progress
- Review and make a pull request with your changes

### Using [`du_MY.lang`](../src/main/resources/assets/hud/lang/du_MY.lang)
- Download the dummy language file <a href="https://github.com/mccreery/better-hud/raw/master/src/main/resources/assets/hud/lang/du_MY.lang" download>(direct)</a>
- Rename it to `<locale>.lang`

### Translating
- Skip lines without a `=`, they are comments
- To translate a line, remove the `#` and translate the text after the `=`
- If the translation is the same as the original, just remove the `#`
