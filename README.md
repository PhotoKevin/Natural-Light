# Natural-Light
## A tool for outdoor photographers

Natural Light is an Android application to show where the sun will be on any given date/time/location so you can plan your shoot.

## Why this application
There are other apps that do pretty much what this one does. Why pick it? The base reasons are privacy and cost. I wrote the application strictly for my own use. As such, it collects zero information and it costs zero dollars. My privacy thoughts extend to using Open Street Maps instead of Google Maps. 

## Typical use case

You're planning a trip to Mackinac Island in June and you want to get photos of Arch Rock. You scroll and zoom the map view to that location. Tapping the clock icon, you set the day you'll be there. Natural light shows you the directions of Sunrise and Sunset so you know when the Golden Hour will be. This isn't your only excursion so you hit the Add button and enter a title.  Now you can come back to this by simply selecting it from your list of saved locations.

Looking at the display, you see a yellow line and dot showing the position of the sun for the displayed time. Sweep your finger back and forth over the date/time displayed to adjust the time and see how the sun moves.

If you want details, hop over to the Details screen to get the exact elevation and azimuth of the sun.

## Issues

My decision to avoid Google maps due to my distrust of their privacy polices led me to Roman Iakovlev's Timehape library. This works entirely offline but has a substantial initialization cost of more than 10 seconds on my phone. In computer time that's an eternity. The application is usable during that time, but works in UTC. Once initialization has completed, the display automatically updates to local time. 

## Credits

As mentioned earlier, the maps come from the amazing [Open Steet Map](https://www.openstreetmap.org) project.

Timezone data is from Roman Iakovlev's [Timeshape](https://github.com/RomanIakovlev/timeshape) library.

Astronomical calculations are based on Jean Meeus' Astronomical Algorithms 2nd edition ISBN 978-0943396613. 


Country Bounding Boxes are from CplPearce
https://gist.github.com/cplpearce/3bc5f1e9b1187df51d2085ffca795bee
