# Static Deformations of Composite Shells  

### Stress strain state analyzer of a rectangular plate with cylindrical corrugations  

### Demonstration  

![screenshot](https://raw.githubusercontent.com/Hitonoriol/SDoCS-Thesis/master/screenshots/screenshot1.png)

---

### Build process

**Requirements:**  

* JDK 8  
* GNU make  
* gfortran  

For Windows users MSYS2 ports of utils listed above (excluding JDK) are recommended.  

This project uses gradle as its build system.  
Use `gradlew` on windows or `./gradlew` on linux to invoke gradle wrapper.  
Only one syntax variant will be used below.  

**Build .jar:**  

```
./gradlew makeNativeLib dist
```

**Run:**  
```
./gradlew makeNativeLib run
```

---