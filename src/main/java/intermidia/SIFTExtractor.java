package intermidia;

import java.io.File;
import java.io.FileWriter;

import org.openimaj.feature.ByteFV;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.video.processing.shotdetector.VideoKeyframe;
import org.openimaj.video.xuggle.XuggleVideo;

import TVSSUnits.Shot;
import TVSSUnits.ShotList;
import TVSSUtils.KeyframeReader;

public class SIFTExtractor 
{
	/*Input format: <video file> <keyframe indexes file> <sift keypoints file>*/
    public static void main( String[] args ) throws Exception
    { 	
    	XuggleVideo source = new XuggleVideo(new File(args[0]));    	    	    	
    	ShotList shotList = KeyframeReader.readFromCSV(source, args[1]);
    	FileWriter output = new FileWriter(args[2]);
    	
    	int shotNum = 0;
    	for(Shot shot: shotList.getList())
    	{ 	
    		int kfNum = 0;
    		for(VideoKeyframe<MBFImage> keyframe: shot.getKeyFrameList())
    		{
    			System.out.println("Processing Shot " + shotNum);
    			DoGSIFTEngine siftEngine = new DoGSIFTEngine();
    			LocalFeatureList<Keypoint> frameKeypoints = siftEngine.findFeatures(keyframe.imageAtBoundary.flatten());
    			//If generate keyframes option is set
    			if(args[3] != null && args[3].compareTo("-k") == 0)
    			{
    				String folder = args[4];
    				String keyframeName = "s" + String.format("%04d", shotNum) + "kf" + String.format("%04d", kfNum) + ".jpg";
    				ImageUtilities.write(keyframe.imageAtBoundary, new File(folder + keyframeName));
    			}
    			for(Keypoint keypoint: frameKeypoints)
    			{
    				//Prints shot number
    				output.write(Integer.toString(shotNum));
    				ByteFV featureVector = keypoint.getFeatureVector();
    				//Prints feature vector
    				for(int j = 0; j < featureVector.length(); j++)
    				{
    					output.write(" " + featureVector.get(j));
    				}
    				output.write("\n");
    			}
    			kfNum++;
    		}
    		shotNum++;
    	}
    	output.close();
    	source.close();
    }
}
