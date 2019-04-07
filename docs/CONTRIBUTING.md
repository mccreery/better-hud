# Contributing

## Pull Requests
Contribute using a pull request if possible. In most cases you should PR into the `develop` branch, which is the most current for the primary Minecraft version. Releases are merged into the corresponding Minecraft version branch, e.g. `1.12.2`. Ongoing ports are suffixed `-port`. This repository has no `master` branch, `develop` is the default branch.

## Code Style
If you want to contribute code to the project, the main restriction is to use Egyptian-style braces:

```java
if(a == b) {
    return 0;
}
```

At the moment there is a bit of inconsistency with tabs and spaces; if you edit an existing file please follow its indent style (visible whitespace is useful), and don't leave trailing whitespace. New files should be indented with spaces. Maybe at some point I'll fix em all, who knows ¯\\\_(ツ)\_/¯

## Adding Translations
Find the locale code for your language [here](https://minecraft.gamepedia.com/Language#Available_languages).

- Create a file (if it doesn't exist) in [`src/main/resources/assets/hud/lang/`](../src/main/resources/assets/hud/lang) called `<locale>.lang`
- Run the Python script [`normalizelang.py`](../normalizelang.py)
- Reopen or reload `<locale>.lang` to see the list of untranslated lines
- Translate as described below
- Re-run [`normalizelang.py`](../normalizelang.py) to see translation progress
- Review and make a pull request with your changes

### Without a Pull Request
If you aren't familiar with Git, you can download <a href="https://github.com/mccreery/better-hud/raw/master/src/main/resources/assets/hud/lang/du_MY.lang" download>the `du_MY` dummy language file</a> and edit it directly. Send it my way however you see fit.

### Translating
- Skip lines without a `=`, they are comments
- To translate a line, remove the `#` and translate the text after the `=`
- If the translation is the same as the original, just remove the `#`
