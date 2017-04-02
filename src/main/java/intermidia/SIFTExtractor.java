package intermidia;

import java.io.FileWriter;

import org.openimaj.feature.ByteFV;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.MBFImage;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.video.processing.shotdetector.VideoKeyframe;

import TVSSUnits.Shot;
import TVSSUnits.ShotList;
import TVSSUtils.KeyframeReader;

public class SIFTExtractor 
{
	/*Usage: <in: keyframe folder> <out: sift keypoints file>*/
    public static void main( String[] args ) throws Exception
    { 	
    	ShotList shotList = KeyframeReader.readFromFolder(args[0]);
    	FileWriter output = new FileWriter(args[1]);
    	
    	int shotNum = 0;
    	for(Shot shot: shotList.getList())
    	{ 	
    		for(VideoKeyframe<MBFImage> keyframe: shot.getKeyFrameList())
    		{
    			//System.out.println("Processing Shot " + shotNum);
    			DoGSIFTEngine siftEngine = new DoGSIFTEngine();
    			LocalFeatureList<Keypoint> frameKeypoints = siftEngine.findFeatures(keyframe.imageAtBoundary.flatten());
    			
    			//If generate keyframes option is set  TODO REMOVE IT SOMETIME
    			/*if(args[3] != null && args[3].compareTo("-k") == 0)
    			{
    				String folder = args[4];
    				String keyframeName = "s" + String.format("%04d", shotNum) + "kf" + String.format("%04d", kfNum) + ".jpg";
    				ImageUtilities.write(keyframe.imageAtBoundary, new File(folder + keyframeName));
    			}TODO END*/
    			
    			//If the keyframe is empty, generate a dummy sift point.
    			if(frameKeypoints.size() == 0)
    			{
    				output.write(Integer.toString(shotNum));
    				//Prints dummy feature vector
    				for(int j = 0; j < 128; j++)
    				{
    					output.write(" " + ((byte)127));
    				}
    				output.write("\n");    				
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
    		}
    		shotNum++;
    	}
    	output.close();
    }
}
