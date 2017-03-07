package application;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class JFXController {
	// FXML buttons
	@FXML
	private Button btnStartCamera;
	// the FXML ImageFrame
	@FXML
	private ImageView ivCameraFrame;
	//the FXML Checkbox
	@FXML
	private CheckBox chbxCanny;
	// canny threshold value
	@FXML
	private Slider sliThres;
	
	
	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// the OpenCV object that performs the video capture
	private VideoCapture capture = new VideoCapture();
	// a flag
	private boolean cameraActive;
	
	
	

	@FXML
	protected void startCamera(){
		
		
		if (!this.cameraActive){
			// start the video capture
			this.capture.open(0);  //0 - notebook internal webcam/ 1 - USB Webcam
			
			// is the video stream available?
			if (this.capture.isOpened())
			{
				this.cameraActive = true;
				
				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {
					
					@Override
					public void run()
					{
						Image imageToShow = grabFrame();
						ivCameraFrame.setImage(imageToShow);
					}
				};
				
				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
				
				this.btnStartCamera.setText("Stop Camera");
	
			}
			else
			{
				// log the error
				System.err.println("Failed to open the camera connection...");
			}
		}else{
			this.cameraActive = false;
			this.btnStartCamera.setText("Start Camera");

			// stop the timer
			try
			{
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				// log the exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
			
			// release the camera
			this.capture.release();
		}
	}
	private Image grabFrame()
	{
		// empty Image object
		Image imageToShow = null;
		// new Mat object
		Mat frame = new Mat();
		
		// check if the capture is open
		if (this.capture.isOpened())
		{
			try
			{
				// read the current frame
				this.capture.read(frame);
				
				// if the frame is not empty, process it
				if (!frame.empty())	{
					if (this.chbxCanny.isSelected()){
					    frame = this.doCanny(frame);
					}
					// convert the Mat object (OpenCV) to Image (JavaFX)
					imageToShow = mat2Image(frame);
				}			
			}
			catch (Exception e){
				// log the (full) error
				System.err.print("ERROR");
				e.printStackTrace();
			}
		}	
		return imageToShow;
	}
	
	private Mat doCanny(Mat frame)
	{
		// init
		Mat grayImage = new Mat();
		Mat detectedEdges = new Mat();

		// convert to grayscale
		Imgproc.cvtColor(frame, grayImage, Imgproc.COLOR_BGR2GRAY);
		
		// reduce noise with a 3x3 kernel
		Imgproc.blur(grayImage, detectedEdges, new Size(3, 3));
		
		// canny detector, with ratio of lower:upper threshold of 3:1
		Imgproc.Canny(detectedEdges, detectedEdges, this.sliThres.getValue(), this.sliThres.getValue() * 3);
		
		// using Canny's output as a mask, display the result
		Mat dest = new Mat();
		frame.copyTo(dest, detectedEdges);
		
		return dest;
	}
	
	
	// function converts image from Mat object to Image object
	private Image mat2Image(Mat frame)
	{
		// create a temporary buffer
		MatOfByte buffer = new MatOfByte();
		// encode the frame in the buffer, according to the PNG format
		Imgcodecs.imencode(".png", frame, buffer);
		// build and return an Image created from the image encoded in the
		// buffer
		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}
	
}
