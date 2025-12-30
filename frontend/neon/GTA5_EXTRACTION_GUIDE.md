# 🎮 GTA 5 Car Model to GLB Conversion Guide

## 📌 Overview

This guide shows you how to download **pre-converted GTA 5 car models** from [GTA5-Mods.com](https://www.gta5-mods.com/vehicles) and convert them to **GLB format** for use in our vehicle customization system.

### ✅ Why This Approach?
- **No GTA V game installation needed** (perfect for Mac!)
- **Models already have modelers' conversions** to FBX/OBJ
- **Full customization support**: paint, rims, spoilers, bumpers, exhaust, interior, engine, suspension
- **Game-ready quality** with proper mesh separation
- **Fast workflow** - download → convert → use (30 minutes total)

### 🎯 Target Car: Mercedes-AMG GT63 S
We'll use the **Mercedes-AMG GT63 S** from GTA5-Mods as the example because it has:
- ✅ Detailed body geometry
- ✅ Separate door meshes
- ✅ Multiple wheel options pre-modeled
- ✅ Spoiler variants
- ✅ Clean topology for customization

---

## 📥 STEP 1: Download Mercedes-AMG GT63 from GTA5-Mods

### Direct Download Link
🔗 **https://www.gta5-mods.com/vehicles/mercedes-amg-gt63-s-coupe**

### Steps:
1. Click **"Download"** button (green button on the page)
2. Choose a mirror (any works)
3. A ZIP file will download (usually named something like `mercedes-amg-gt63.zip`)
4. **Extract the ZIP** on your Mac (double-click it)

### What You'll Get
The extracted folder will contain files like:
```
mercedes-amg-gt63/
├── mercedes.yft          (Main car model)
├── mercedes.ytd          (Textures/materials)
├── mercedes_hi.yft       (High detail version - optional)
└── [other files]
```

> **Note**: These are GTA V native format files (.yft = GTA model, .ytd = GTA texture)

---

## 🛠️ STEP 2: Install Blender (Mac)

### Download & Install
1. Go to **https://www.blender.org/download/**
2. Click **"Download Blender 4.1"** (or latest version)
3. Select **macOS** (Intel or Apple Silicon depending on your Mac)
4. Download the `.dmg` file
5. Double-click and drag **Blender** to Applications folder

### Verify Installation
```bash
# Check if Blender is installed
/Applications/Blender.app/Contents/MacOS/Blender --version
```

---

## � STEP 3: Install Sollumz Blender Add-on

The **Sollumz** plugin lets Blender read GTA files (.yft, .ydr, etc.)

### Installation Steps:

1. **Download Sollumz:**
   - Go to: https://github.com/Sollumz/Sollumz/releases
   - Download the **latest release ZIP** (e.g., `Sollumz-v1.x.x.zip`)
   - **Do NOT extract it** - keep it as a ZIP file

2. **Open Blender**
   - Launch Blender from Applications

3. **Install the add-on:**
   - Go to: **Blender → Preferences** (Blender → Preferences on Mac)
   - Click **"Add-ons"** tab (left sidebar)
   - Click **"Install..."** button (top right)
   - Navigate to the downloaded `Sollumz-*.zip` file
   - Click **"Install Add-on"**

4. **Enable the add-on:**
   - Search for "Sollumz" in the add-ons search box
   - Check the ✅ checkbox to enable it
   - You should see: **"Import-Export: Sollumz"**

5. **Verify:**
   - Go to **File → Import** menu
   - You should see **".yft, .ydr, .ydd Sollumz Import"** option

---

## 🚗 STEP 4: Import Mercedes Model into Blender

### Import the Model:

1. **Open Blender** (create new file or use default)

2. **Delete default cube:**
   - Select the cube (it's selected by default)
   - Press **Delete** or **X** key
   - Confirm "Delete"

3. **Import the Mercedes model:**
   - Go to: **File → Import → .yft, .ydr, .ydd Sollumz Import**
   - Navigate to your Mercedes folder (where you extracted the ZIP)
   - Select **`mercedes.yft`** file
   - Click **"Import YFT"** or **"Open"**

4. **Wait for import** (30 seconds - 2 minutes depending on detail)

5. **You'll see the car!** 🎉
   - The Mercedes will appear in the 3D viewport
   - You can rotate it with middle mouse button
   - Scroll to zoom in/out

### Inspect the Model:
- Look at **Outliner panel** (right side) to see all parts:
  ```
  mercedes_body      (main chassis - paint color applied here)
  mercedes_door_fl   (front left door)
  mercedes_door_fr   (front right door)
  mercedes_wheel_lf  (left front wheel)
  mercedes_wheel_rf  (right front wheel)
  mercedes_glass     (windows - transparent)
  mercedes_headlight (lights)
  mercedes_interior  (cabin)
  [etc]
  ```

---

## 💾 STEP 5: Export to GLB Format

GLB is the format our customization system uses. It's a single-file 3D format.

### Export Steps:

1. **Select all objects:**
   - Press **A** key (select all)
   - You should see everything highlighted in orange

2. **Export to GLB:**
   - Go to: **File → Export as glTF 2.0 (.glb/.gltf)**
   - A save dialog appears

3. **Configure export settings:**
   - **Filename**: Change to `car.glb` (important: must be `car.glb`)
   - **Format**: Select **"glTF Binary (.glb)"** 
   - **Include** section - Check these boxes:
     - ✅ **Materials** (so colors/textures are included)
     - ✅ **Textures** (for material details)
     - ✅ **Original Normals** (smooth appearance)
     - Leave other options as default

4. **Click "Export glTF 2.0"**

5. **You'll get `car.glb`** file on your desktop or Downloads

---

## 📂 STEP 6: Add Model to Your Project

Now move the GLB file to your project:

```bash
# Copy the car.glb to your project's public folder
cp ~/Downloads/car.glb /Users/syed/Documents/PSE/nebula/frontend/neon/public/models/car.glb

# Verify it's there
ls -lh /Users/syed/Documents/PSE/nebula/frontend/neon/public/models/car.glb
```

---

## 🚀 STEP 7: Test in Your App

1. **Start the dev server:**
   ```bash
   cd /Users/syed/Documents/PSE/nebula/frontend/neon
   npm run dev
   ```

2. **Open in browser:**
   - Go to: **http://localhost:3000**

3. **You should see:**
   - Mercedes model loading (with spinner initially)
   - Full 360° rotation capability (drag to rotate)
   - Customization panel on the right with all options:
     - 🎨 **Paint** (changes body color)
     - ⭕ **Rims** (wheel styles)
     - 🛫 **Spoilers** (aerodynamic parts)
     - 🚗 **Bumpers** (front/rear)
     - 💨 **Exhaust** (exhaust systems)
     - 🪑 **Interior** (cabin colors)
     - ⚙️ **Engine** (performance tier)
     - 🔧 **Suspension** (ride height/setup)

4. **Try customizing:**
   - Click paint colors → see Mercedes color change in real-time
   - Adjust other parts → prices update dynamically
   - Rotate car → verify model quality

---

## 🎨 How Paint Works

The system intelligently applies materials:

| Part Name | Gets | Result |
|-----------|------|--------|
| Contains "body" | Paint color | Car body changes color |
| Contains "wheel" | Chrome metallic | Shiny wheels |
| Contains "glass" | Transparent | See-through windows |
| Contains "interior" | Dark leather | Cabin color |
| Contains "light" | Emissive yellow/red | Glowing lights |

**You don't need to do anything** - the system auto-detects parts by name!

---

## 🐛 Troubleshooting

### "Model doesn't load"
- Check that `car.glb` is at: `public/models/car.glb`
- Check browser console (F12) for errors
- File size should be under 50MB

### "Model loads but looks dark"
- The lighting is correct! GTA models look dark because they're game-optimized
- Our lighting system will adjust it
- This is normal

### "Colors don't change when I select paint"
- Parts might be named differently than expected
- Check the Blender Outliner to see exact mesh names
- Modify `vehicleData.ts` if needed to match mesh names

### "File size is too large"
- The model might be too detailed
- In Blender, you can delete low-poly LOD versions
- Export only the high-quality main model

---

## ⏱️ Total Time Estimate

| Step | Time |
|------|------|
| Download model | 2 min |
| Install Blender | 5 min |
| Install Sollumz | 3 min |
| Import model | 2 min |
| Export to GLB | 2 min |
| Add to project | 1 min |
| **TOTAL** | **~15 minutes** |

---

## ✅ Checklist Before You Start

- [ ] Downloaded `car.glb` and placed it in `public/models/car.glb`
- [ ] Development server is running (`npm run dev`)
- [ ] Browser is open to `http://localhost:3000`
- [ ] You can rotate the car with your mouse
- [ ] Paint colors change when you select them
- [ ] Price updates as you customize parts

---

## 🎯 What's Supported in This System

Your Mercedes will support:

✅ **8 Customization Categories:**
- Paint (6 colors: Metallic Red, Midnight Black, Pearl White, Electric Blue, Sunset Orange, Chrome Silver)
- Rims (5 wheel styles: Sport A, Sport B, Racing Pro, Chrome Luxury, Carbon Fiber)
- Spoilers (5 options: None, Lip, Carbon Wing, GT Wing, Ducktail)
- Bumpers (4 styles: Stock, Sport, Carbon, Wide Body)
- Exhaust (4 systems: Stock, Sport, Titanium, Racing)
- Interior (5 colors: Standard, Black Leather, Red Leather, Carbon Sport, Luxury Tan)
- Engine (4 upgrades: Stock, Turbo, Supercharger, Race)
- Suspension (4 setups: Stock, Lowered, Coilovers, Air Ride)

✅ **Real-time 3D Preview** - See changes instantly

✅ **Dynamic Pricing** - Base $45,000 + upgrades

✅ **Professional Lighting** - 4-point cinema lighting setup

✅ **Interactive Camera** - Full 360° rotation, zoom, pan

---

## � Next: Import Other Cars

Once Mercedes works, you can repeat these steps for any GTA 5 car:

1. Find another car on https://www.gta5-mods.com/vehicles
2. Look for one with **FBX/3D Model in description** for easier conversion
3. Download, extract, import to Blender, export to GLB
4. Name it differently (e.g., `car_porsche.glb`)
5. Update project to load the new model

---

## ⚠️ Legal Note

**GTA 5-Mods Downloads:**
- ✅ Free to use for personal/learning projects
- ✅ Fine for portfolios and demos
- ❌ NOT for commercial applications (check specific mod license)
- ❌ Always credit the original modeler

**Always check the mod page for the specific license!**

---

**Good luck! 🚗✨ Your Mercedes-AMG is about to become the ultimate car customizer!**
