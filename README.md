# Sans Mod (Fabric 1.21.11, client-side)

A client-side Fabric mod with a Sans-inspired GUI (open with **Right Shift**), saved config, and keybinds
(Gaster Blaster `G`, Bone `B`, Teleport `V`, Cycle Target `H`, Toggle Auto Dodge `J`).
All keys can be changed in the GUI (Settings tab) or in Minecraft's Controls menu.

You do **not** need to install anything on your computer to build it. GitHub builds it for you.

---

## 1. Upload the project to GitHub

1. Make a free account at https://github.com and sign in.
2. Click the **+** (top right) -> **New repository**. Name it `sansmod`, leave it **Public** or **Private**, and click **Create repository**.
3. On the new empty repository page, click **uploading an existing file**.
4. Unzip `SansMod-Complete.zip` on your computer, open the unzipped folder, select **everything inside it**
   (including the hidden `.github` folder) and drag it into the GitHub page.
   - The files `build.gradle`, `gradlew` and the folders `src`, `gradle`, `.github` must be at the **top level** of the repository, not inside an extra folder.
   - If you can't see `.github`, enable "Show hidden files" in your file manager (Mac: `Cmd+Shift+.`).
   - Drag-and-drop in the browser does upload hidden folders in most browsers; check the repo afterwards for `.github/workflows/build.yml`. If it's missing, use **Add file -> Create new file**, type `.github/workflows/build.yml` as the name, and paste in the contents from your copy.
5. Scroll down and click **Commit changes**.

## 2. Run the build

The build starts **automatically** every time you upload/push, and on pull requests.
To start it by hand:

1. Open your repository and click the **Actions** tab.
2. Click **Build Sans Mod** in the left list.
3. Click **Run workflow** -> **Run workflow**.
4. Wait about 3-6 minutes (the first run is slowest). A green check means success; a red X means it failed (click it to read the log).

## 3. Download the JAR

1. In **Actions**, click the finished run.
2. Scroll to **Artifacts** at the bottom and click **SansMod-jar**. A ZIP downloads.
3. Unzip it. Inside is `sansmod-0.1.0.jar`. That is the mod.

## 4. Install in Minecraft

1. Install **Fabric Loader** for Minecraft **1.21.11** from https://fabricmc.net/use/installer/ and run Minecraft once with the Fabric profile.
2. Download **Fabric API** for 1.21.11 from https://modrinth.com/mod/fabric-api and put it in your mods folder.
3. Put `sansmod-0.1.0.jar` in the mods folder too:
   - Windows: press `Win+R`, type `%appdata%\.minecraft\mods`
   - macOS: `~/Library/Application Support/minecraft/mods`
   - Linux: `~/.minecraft/mods`
   (Create the `mods` folder if it doesn't exist.)
4. Launch Minecraft with the **Fabric** profile, join a world, and press **Right Shift**.

## Building on your own computer (optional)

Requires Java 21. Run `./gradlew build` (macOS/Linux) or `gradlew.bat build` (Windows).
The JAR appears in `build/libs/` (use `sansmod-0.1.0.jar`, not the `-sources` one).
The first run downloads `gradle-wrapper.jar` automatically.

## Troubleshooting

- **Red X in Actions:** open the run, click the failed step, and read the error. If it says a dependency version doesn't exist,
  check the newest versions for 1.21.11 at https://fabricmc.net/develop and update `gradle.properties`.
- **Game crashes on start:** make sure you used Minecraft 1.21.11, Fabric Loader 0.18.1+, and Fabric API.
- **Stage note:** this is stage 1 of the mod: GUI, config and keybinds work; the ability keys are registered but the abilities themselves are not implemented yet (same as in your original project).
