# greenscreen
A greenscreen program written in Java that utilizes the BufferedImage class.

/// Purpose and Overview ///

This program takes in a green screen image (some object with a green background behind it) and a replacement image.
It replaces all the green (or similar) pixels in the green screen image with the replacement image pixels. The resulting image is output and named as the green screen image with 'merged' appended.

/// How to use ///

Run the program in a command line with JRE installed. When the program starts, it will request the green screen image.

  The image will be input exactly as it exists in the explorer. For example, a .jpg named 'screen' will be input as screen.jpg. The filetype 
  must be included, as it's possible to also have .png in addition to .jpg. The replacement image will be input the same way.

Once the two images are selected, the user specifies a tolerance to use.
The tolerance is a float value (also acceptable as an int input) that determines how strict the program will be when replacing green pixels.
It ranges from 1.0 to 10.0, 1.0 being the scrictest possible option when selecting green pixels.

/// Output ///

The output of the program will be the name of the green screen image with 'merged' appended, and will be of type .jpg. If the final image already exists in the directory, it will be appended with a number, starting with 1. If that file exists, the number is incremented by one until it finds a filename that is not in use.
