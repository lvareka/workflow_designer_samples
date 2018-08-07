package cz.zcu.kiv.eeg.basil.workflow;

import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockExecute;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockInput;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockOutput;
import cz.zcu.kiv.WorkflowDesigner.Annotations.BlockType;

import java.util.Arrays;
import java.util.List;

/**
 * Average a list of epochs using one stimuli marker
 * @author lvareka
 *
 */
@BlockType(type="NeuralNetClassifier",family = "Classification")
public class NeuralNetClassifierBlock {

	@BlockInput(name = "Markers",type="EEGMarker[]")
	private List<EEGMarker> markers;

	@BlockInput(name = "EEGData", type = "EEGDataList")
	private EEGDataPackageList epochs;

	public NeuralNetClassifierBlock(){
		//Required Empty Default Constructor for Workflow Designer
	}

	@BlockExecute
    public void process(){
		ITrainCondition trainCondition = new ErpTrainCondition();
		for (EEGDataPackage dataPackage : epochs.getEegDataPackage()) {

			String marker;
			if(dataPackage.getMarkers() == null || dataPackage.getMarkers().get(0) == null)
				continue;
			marker = dataPackage.getMarkers().get(0).getName();

			FeatureVector fv = new FeatureVector();
			WaveletTransformFeatureExtraction dwt = new WaveletTransformFeatureExtraction();

			FeatureVector features = dwt.extractFeatures(dataPackage);
			fv.addFeatures(features);

			trainCondition.addSample(fv, "S  2", marker);


		}
		IClassifier classification = new SDADeepLearning4jClassifier();
		classification.train(trainCondition.getFeatureVectors(), 10);
    }


    public List<EEGMarker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<EEGMarker> markers) {
        this.markers = markers;
    }

    public EEGDataPackageList getEpochs() {
        return epochs;
    }

    public void setEpochs(EEGDataPackageList epochs) {
        this.epochs = epochs;
    }


}
