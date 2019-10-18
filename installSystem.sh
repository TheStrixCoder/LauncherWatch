#!/bin/bash

# CHANGE THESE FOR YOUR APP
app_package="com.bid.launcherwatch"
dir_app_name="MyLauncher"
MAIN_ACTIVITY="MainActivity"

ADB="adb" # how you execute adb
ADB_SH="$ADB shell su -c" # this script assumes using `adb root`. for `adb su` see `Caveats`

path_sysapp="/system/app" # assuming the app is priviledged
apk_host="./app/build/outputs/apk/debug/app-debug.apk"
apk_name=$dir_app_name".apk"
apk_target_dir="$path_sysapp/$dir_app_name"
apk_target_sys="$apk_target_dir/$apk_name"

# Delete previous APK
rm -f $apk_host

# Compile the APK: you can adapt this for production build, flavors, etc.
./gradlew assembleDebug || exit -1 # exit on failure

# Install APK: using adb root
$ADB_SH "mount -o rw,remount /system"
$ADB_SH "chmod 777 /system/lib/"
$ADB_SH "mkdir -p /sdcard/tmp" 2> /dev/null
$ADB_SH "mkdir -p $apk_target_dir" 2> /dev/null
$ADB push $apk_host /sdcard/tmp/$apk_name 2> /dev/null
$ADB_SH "mv /sdcard/tmp/$apk_name $apk_target_sys"
$ADB_SH "rmdir /sdcard/tmp" 2> /dev/null

# Give permissions
$ADB_SH "chmod 755 $apk_target_dir"
$ADB_SH "chmod 644 $apk_target_sys"

#Unmount system
$ADB_SH "mount -o remount,ro /system"

# Stop the app
$ADB shell "am force-stop $app_package"

# Re execute the app
$ADB shell "am start -n \"$app_package/$app_package.$MAIN_ACTIVITY\" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER"
