COCO-Accessory
==============

[![Build Status](https://travis-ci.org/soarcn/COCO-Accessory.svg)](https://travis-ci.org/soarcn/COCO-Accessory)

This project provides sets of collection of source codes, utilities and snippets for Android developemnt.

There are 4 sub-modules, and you could use each or all of them together base on your real needs.
 
Please be aware that this project still under development, so classes and interface would be changed at any moment.
 
Most of classes in this project are from other opensource projects. 

Most of classes are compatible in android 2.3+

gradle config

    ```groovy
    compile 'com.cocosw.accessory:connectivity:+@aar' 
    compile 'com.cocosw.accessory:views:+@aar' 
    compile 'com.cocosw.accessory:utils:+@aar' 
    ```

Adapter
--------------




Connectivity
--------------

- A single class provide an simple way to get/observe mobile phone network connectivity.
- Example
```java    
    NetworkConnectivity.getInstance(context).addNetworkMonitorListener(new NetworkMonitorListener() {
    
        /**
         * connection established
         */
        public void connectionEstablished();

        /**
         * connection lost
         */
        public void connectionLost();

        /**
         * connecting to network
         */
        public void connectionCheckInProgress();
    });
```

Views
--------------

- This is a collection of android ui components. Some of them are bug fixed ones and are functional enhanced ones.

com.cocosw.accessory.views
com.cocosw.accessory.views.adapter	 
com.cocosw.accessory.views.adapterview	 
com.cocosw.accessory.views.complex	 
com.cocosw.accessory.views.drawable	 
com.cocosw.accessory.views.layout	 
com.cocosw.accessory.views.textview	 
com.cocosw.accessory.views.widgets


Utils
---------------

- A collection of android utils not directly related to UI components


