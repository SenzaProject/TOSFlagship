# TOS Flagship (Nez Optimise)

Aplikasi Android untuk mengatur tweak modul **TOS Flagship Tweaks (A16)** oleh Nezuka.
UI-nya memakai WebUI asli modul (`app/src/main/assets/webroot/index.html`) yang dijalankan
di WebView dengan jembatan `ksu` buatan sendiri (root lewat `su`).

- Butuh root (Magisk / KernelSU / APatch).
- Saat pertama dibuka, jika modul belum terpasang, aplikasi menawarkan memasang modul bawaan
  (`assets/module.zip`). Reboot setelah pemasangan.
- Build: push ke GitHub, lalu ambil APK di Actions -> Artifacts (`TOSFlagship-APK`).

Logo: `logo_1024.png` / `logo_512.png`.
