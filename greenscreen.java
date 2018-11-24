// File name: greenscreen.java
// Author: George Perez
// Description: This program takes an image from the user and acts as a green screen to replace a range of green
//              pixels with another image provided by the user. View more specs inside the README.

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import java.awt.Graphics2D.*;

public class greenscreen
{
  /****************************************************************
  /*   FUNCTION NAME: Read_Image
  /*   Description: Returns a BufferedImage based on the file name provided
  /*   PARAMETERS: String file
  /*   RETURN VALUES: BufferedImage image
  /***************************************************************/
  public static BufferedImage Read_Image(String file)
  {
    BufferedImage image = null; // Declare the image

    // Get the image data from the file
    try
    {
      image = ImageIO.read(new File(file));
    }
    catch(IOException e) {}

    // Track the loading of the image
    MediaTracker tracker = new MediaTracker(new Component() {});
    tracker.addImage(image, 0);
    try
    {
      tracker.waitForID(0);
    }
    catch(InterruptedException e) {}

    return image;
  }

  /****************************************************************
  /*   FUNCTION NAME: fileNameCheck
  /*   Description: Checks the file name provided along with the file type and produces a final
  /*    file name with "merged" appended. If that file already exists, the function appends it with a number. If the file name
  /*    and number exist, it will count up until it reaches a file name that is not taken and will return that string.
  /*   PARAMETERS: String inputString, String fileType
  /*   RETURN VALUES: inputString
  /***************************************************************/
  public static String fileNameCheck(String inputString, String fileType)
  {
    int fileCount = 1; // For checking the end of the image names (in case there are multiple)
    inputString = inputString.substring(0, inputString.length() - 4); // Getting filename without extension
    inputString += "merged"; // Appending merged

    if(fileType.substring(0, 1) != ".") // If the filetype provided doesn't begin with .
    {
      fileType = ".jpg"; // Default to .jpg
    }

    File workingFile = new File(inputString + fileType);
    File workingNumberedFile = new File(inputString + "1" + fileType);

    if(workingFile.exists() && !workingNumberedFile.exists()) // If the file currently exists without a number
    {
      inputString += fileCount;
    }
    else if(!workingFile.exists()) // If the file doesn't exist
    {
      inputString += fileType;
      return inputString;
    }
    else if(workingNumberedFile.exists()) // If the file is already numbered
    {
      while(workingNumberedFile.exists())
      {
        fileCount++;
        inputString += fileCount; // Appending the current count onto the file name
        workingNumberedFile = new File(inputString + fileType); // Changing the workingNumberedFile name so that can be checked again
        if(fileCount > 9) // If the image number is two digits
        {
          inputString = inputString.substring(0, inputString.length() - 2); // Removing the old number to enable proper updating
        }
        else if(fileCount > 99) // If the image number is three digits
        {
          inputString = inputString.substring(0, inputString.length() - 3); // Removing the old number to enable proper updating
        }
        else // If the image number is one digit
        {
          inputString = inputString.substring(0, inputString.length() - 1); // Removing the old number to enable proper updating
        }
      }
      inputString += fileCount; // Appending the fileCount to the end of the file name
    }

    inputString += fileType; // Appending the filetype to the end of the name

    return inputString;
  }

  /****************************************************************
  /*   FUNCTION NAME: replace
  /*   Description: Returns a boolean indicating whether or not to replace a pixel based on the algorithm
  /*   PARAMETERS: int red, int green, int blue, float tolerance
  /*   RETURN VALUES: boolean
  /***************************************************************/
  public static boolean replace(int red, int green, int blue, float tolerance)
  {
    boolean gray = false;
    // If the range between all RGB values is beneath a certain threshold, don't replace since it's a gray value (black, gray, white)
    if((Math.abs(red - green) <= (15 + tolerance)) && Math.abs(blue - green) <= (15 + tolerance))
    {
      gray = true;
    }

    if((green > (red + (10 * tolerance))) && // If green is the dominant color value
       (green > (blue + (10 * tolerance))) &&
       !gray)
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  public static void main(String[] args) throws IOException
  {
    String gsImageString; // Name of the green screen image
    String repImageString; // Name of the replacement image
    String toleranceString; // String for tolerance
    float tolerance = 5.0f; // Amount of tolerance provided (defaulted to 5)
    BufferedImage gsImage = null; // The green screen image
    BufferedImage repImage = null; // The replacement image

    // Prompt and read the names of the images
    InputStreamReader stdin = new InputStreamReader(System.in);
    BufferedReader keyboard = new BufferedReader(stdin);

    //////////////////////////////////////////////////////////////////////////////
    // Getting green screen image
    //////////////////////////////////////////////////////////////////////////////

    System.out.print("Enter the filename of the green screen image: ");
    gsImageString = keyboard.readLine(); // Reading input from user to name the file for which to look

    if(gsImageString.length() == 0)
    {
      System.out.println("Please provide a filename for the green screen image. Exiting...");
      return;
    }
    else if(!(gsImageString.substring(gsImageString.lastIndexOf('.') + 1)).equals("jpg") && // If the file type is not jpg or png
           (!(gsImageString.substring(gsImageString.lastIndexOf('.') + 1)).equals("png")))
    {        // If the comparison of the last characters after the . in the input file name returns false
      System.out.println("Green screen image is of an invalid file type (supported file types are .jpg and .png). Exiting...");
      return;
    }
    gsImage = Read_Image(gsImageString); // Creating the green screen image with the file name given by the user

    if(gsImage == null) // If the green screen image is not in the directory
    {
      System.out.println("Green screen image not found. Exiting...");
      return;
    }

    //////////////////////////////////////////////////////////////////////////////
    // Getting replacement image
    //////////////////////////////////////////////////////////////////////////////

    System.out.print("Enter the filename of the replacement image: ");
    repImageString = keyboard.readLine(); // Reading input from user to name the file for which to look

    if(repImageString.length() == 0)
    {
      System.out.println("Please provide a filename for the replacement image. Exiting...");
      return;
    }
    else if(!(repImageString.substring(repImageString.lastIndexOf('.') + 1)).equals("jpg") && // If the file type is not jpg or png
           (!(repImageString.substring(repImageString.lastIndexOf('.') + 1)).equals("png")))
    {        // If the comparison of the last characters after the . in the input file name returns false
      System.out.println("Replacement image is of an invalid file type (supported file type is .jpg). Exiting...");
      return;
    }

    repImage = Read_Image(repImageString); // Creating the replacement image with the file name given by the user

    if(repImage == null) // If the replacement image is not in the directory
    {
      System.out.println("Replacement image not found. Exiting...");
      return;
    }

    //////////////////////////////////////////////////////////////////////////////
    // Getting tolerance
    //////////////////////////////////////////////////////////////////////////////

    System.out.print("Tolerance is how biased the program will be when scanning for green pixels.\nEnter a tolerance between 1.0 and 10.0 (1.0 being very strict on selecting green): ");
    toleranceString = keyboard.readLine();

    if(toleranceString.length() == 0)
    {
      System.out.println("No tolerance input, defaulting to 5.0");
    }
    else
    {
      try
      {
        tolerance = Float.parseFloat(toleranceString); // Reading the tolerance from the user
        if((tolerance < 1.0) || (tolerance > 10.0))    // If outside the range
        {
          System.out.println("Please stay within the range of 1.0 to 10.0. Exiting...");
          return;
        }
      }
      catch(NumberFormatException e)
      { // If the user input something other than a number
        System.out.println("Only numbers are allowed, please refine your tolerance input. Exiting...");
        return;
      }
    }

    //////////////////////////////////////////////////////////////////////////////
    // Scaling the smaller image up to the dimensions of the larger image (if necessary)
    //////////////////////////////////////////////////////////////////////////////

    // If the image dimensions are different from each other, the smaller one will be scaled up the to the larger one's dimensions
    int height = gsImage.getHeight(null); // Defaulting to the green screen image dimensions
    int width = gsImage.getWidth(null);

    if(gsImage.getHeight(null) < repImage.getHeight(null)) // If the repImage has a larger height
    {
      height = repImage.getHeight(null);
    }
    // If they are equal, do nothing

    if(gsImage.getWidth(null) < repImage.getWidth(null)) // If the repImage has a larger width
    {
      width = repImage.getWidth(null);
    }
    // If they are equal, do nothing

    BufferedImage gsImageReplace = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // Create new images for scaling
    BufferedImage repImageReplace = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics gsScaler = gsImageReplace.createGraphics();
    Graphics repScaler = repImageReplace.createGraphics();
    gsScaler.drawImage(gsImage, 0, 0, width, height, null); // Drawing the scaled images
    gsScaler.dispose();
    repScaler.drawImage(repImage, 0, 0, width, height, null);
    repScaler.dispose();


    //////////////////////////////////////////////////////////////////////////////
    // Altering the green screen image using the tolerance to replace certain pixels
    //////////////////////////////////////////////////////////////////////////////

    for (int i = 0; i < width; i++) // For every width pixel
    {
      for(int j = 0; j < height; j++) // For every height pixel
      {
        int rgb = gsImageReplace.getRGB(i, j); // Get the pixel from the green screen image

        int alpha = ((rgb >> 24) & 0xff);
        int red   = ((rgb >> 16) & 0xff);
        int green = ((rgb >>  8) & 0xff);
        int blue  = ((rgb) & 0xff);
        Color testColor = new Color(red, green, blue);

        if(replace(red, green, blue, tolerance)) // If the pixel is supposed to be replaced
        {
          int repRGB = repImageReplace.getRGB(i, j);
          int repRed = ((repRGB >> 16) & 0xff);
          int repGreen = ((repRGB >> 8) & 0xff);
          int repBlue = ((repRGB) & 0xff);
          Color newRGB = new Color(repRed, repGreen, repBlue);

          gsImageReplace.setRGB(i,j, newRGB.getRGB()); // Set the pixel to the same value as the one in the replacement image
        }
      }
    }

    //////////////////////////////////////////////////////////////////////////////
    // Outputting the produced image
    //////////////////////////////////////////////////////////////////////////////

    gsImageString = fileNameCheck(gsImageString, ".jpg"); // Checking file name of the final product

    File mergedFile = new File(gsImageString);
    ImageIO.write(gsImageReplace, "jpg", mergedFile); // Writing the image to the directory

    System.out.println("\nFinished merging!");
    System.out.println(gsImageString + " created in directory");
  }
}
