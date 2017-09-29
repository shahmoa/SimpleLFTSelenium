#!/bin/bash

# useful utility to download selenium chrome driver
# tradition would say you put them in a folder such as /opt/selenium
# but you can choose elsewhere.  Make sure the Selenium script points to the location you choose

if [ -z "$1" ]
then
    echo "Usage:"
    echo "    getDriver.sh driverVersionNumber"
    exit 1
fi

VER=$1

echo "Getting chromedriver for:"
echo "#########################"
echo "     "$VER
echo "#########################"
sudo mkdir $VER
cd $VER
sudo wget -N http://chromedriver.storage.googleapis.com/$VER/chromedriver_linux64.zip -P .

sudo unzip chromedriver_linux64.zip

sudo chmod 755 chromedriver
