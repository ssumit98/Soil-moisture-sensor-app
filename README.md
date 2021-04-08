# Soil Moisture Sensor & App.

# How To Use:
1.Open the file in Arduino IDE.<br>
2.Connect the ESP 8266 WiFI Module to correct port.<br>
3.Change the wifi SSID and Password to your personal one.<br>
4.Now upload the code.<br>
5.When uploading is completed, open serial plotter to see the reading.<br>
6.Place the sensors in the soil near the roots.<br>
7.Now install the app.<br>
8.When app is opened.<br>
9.Select Start new crop or continue previous crop. <br>
10.Accept the Crop by list of name. (For demo, add 2 crops In database. With 1st crops range of suitable moisture 30-50 and 2nd’s range 50-70. <br>
11.Accept the type of water resources.(i.e well,river, cannal, etc by selecting options)<br>
12.Accept the type of land.<br>
13.Accept type of irrigation. (i.e drip, pipe,cannal etc.by selecting options)<br>
14.Accept size of pipe and motar power. <br>
15.Accept time for electricity avaliblity<br>
16.Store all this in database<br>
17.Accept data from ESP8266 wifi module, about the %of moisture
content . Store it in database. <br>
18.Calculate rate of decrease of moisture % using last 24hr readings. (i.e 1st reading - 2nd reading / Time between those readings.<br> 
19.Display number of sensors by their name.(3 sensors)<br>
20.When clicked on their name it will display the current % of moisture, list of previous 5 readings , time when will the % go below the
suitable set range % of the crop.<br>

![WhatsApp Image 2021-04-08 at 10 06 50 PM (1)](https://user-images.githubusercontent.com/82075825/114066213-1a25f900-98b9-11eb-901e-cdc489e0f7f1.jpeg)<br>
![WhatsApp Image 2021-04-08 at 10 06 50 PM](https://user-images.githubusercontent.com/82075825/114066218-1b572600-98b9-11eb-8162-ca661e4a7767.jpeg)


## Summary:

Hello friends, we all know that many cities in India faces frequent drought-like situations. Even populated cities such as Latur have faced such crisis and had to borrow water using train just for drinking needs. Global warming is the cause for this season shift and the farmers are directly affected by it. Their crop yield too has drastically decreased. Their traditional methods are proving to be less trustworthy and also their existing knowledge is leading them to incorrect results. So we ask you now - Are we really facing droughts? Is the yield really decreased due to scarcity of water? The answer to these questions is no. Do you guys know that Israel is a desert country and still it exports dates and cotton to India and even fruits and vegetables to various parts in Europe. Hence, droughts in India are fictional and can be avoided by proper management of water as this is the only time to do so.
From here on comes my solution for helping these unassisted farmers to maximise their crop yield using available amount of water with them. First of all, we will place our basic unit at different places in farm such that WiFi modem remains centered and the units within the range of network. When a successful connection is established, our units will send the sensor data to the Google firebase in real-time using WiFi module. Now, the farmer needs to install an app created by us on their phones. This app will fetch the data from firebase and will show current moisture % and previous recorded readings. In addition to this, it will suggest him when to supply water to crop and also notify if the amount of water exceeds defined limit. This was the basic idea of our solution and what makes it more interesting is that it can be implemented as per economy. Our 1 basic unit costs less than 300 Rs. and at least 4-5 units are required per acre.
If the farmer has bigger budget, we can use relay to control the motor and fully automate the watering system based on predictions that are to be done through analysis of weather forecasting sites and mini weather station that will be too installed on the field.


✌✌✌
