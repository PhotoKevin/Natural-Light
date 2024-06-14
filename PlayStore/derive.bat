magick convert NaturalLight.png -resize 512x512 NaturalLight-512.png

magick NaturalLight.png -crop 1024x500+0+155  -font Arial-Bold -pointsize 100 ^
           -strokewidth 0  -stroke transparent   -undercolor transparent ^
           -size 1024x512 -gravity north  -draw "text 0,195 'Natural Light'" ^
           -font Arial-Italic -pointsize 50 ^
           -draw "text 10,310 'What will the lighting be like?'" ^
  "Natural Light-feature-graphic.png"

