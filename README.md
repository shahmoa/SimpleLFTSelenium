# LeanFT & Selenium
This is a simple script to show using a Selenium test being augmented with LeanFT.

This script has been run using the Chrome driver 2.27.  You can choose others to but just be aware i havent verified it against other.

The ![getDrivers.sh](getDrivers.sh) is a small script to help in pulling down different versions of the Chrome drivers.  Make sure you are in the root directory of your choosing (typically, /opt/selenium) and execute like:
```
./getDrivers.sh 2.36
```

and the driver will be downloaded and extracted to the subfolder 2.36.

If you are making use of a different driver and or location, make sure to reflect the change in the actual script where the Chrome driver is deployed.
