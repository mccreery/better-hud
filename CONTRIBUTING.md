# Contributing

You will need some knowledge of Git to contribute to this project. Alternative instructions for adding translations without using Git or Python are included.

## Adding Translations
To add translations:
- Find the locale code for your language [here](https://minecraft.gamepedia.com/Language#Available_languages)
- Create a file called `<locale>.lang` in the [lang folder](src/main/resources/assets/hud/lang/)
- From the repository root, run the Python script `normalizelang.py`, \
  which will populate the language file
- If you do not have Python installed or don't know Git, you can instead:
  - Download the [`du_MY.lang` file](src/main/resources/assets/hud/lang/du_MY.lang)
  - Rename it to `<locale>.lang` as above
  - Continue with the following steps
- For each line in the file containing an equals sign `=`:
  - If you don't need to or can't translate the line, skip it
  - Otherwise, translate the text after `=` to your language and remove the `#` at the start of the line
- If you're using Git, submit a pull request with your new language file or modified existing language files
- Otherwise, send me a message with the `<locale>.lang` file you created
