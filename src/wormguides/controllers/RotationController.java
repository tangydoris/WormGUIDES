package wormguides.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.converter.NumberStringConverter;

public class RotationController extends AnchorPane implements Initializable {
	@FXML
	private Slider xRotationSlider;
	@FXML
	private Slider yRotationSlider;
	@FXML
	private Slider zRotationSlider;

	@FXML
	private TextField rotateXAngleField;
	@FXML
	private TextField rotateYAngleField;
	@FXML
	private TextField rotateZAngleField;

	private DoubleProperty xRotationAngle;
	private DoubleProperty yRotationAngle;
	private DoubleProperty zRotationAngle;

	public RotationController(DoubleProperty xRotationAngle, DoubleProperty yRotationAngle,
			DoubleProperty zRotationAngle) {
		super();

		this.xRotationAngle = xRotationAngle;
		this.yRotationAngle = yRotationAngle;
		this.zRotationAngle = zRotationAngle;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		assertFXMLNodes();

		xRotationAngle.addListener(getXRotationListener());
		yRotationAngle.addListener(getYRotationListener());
		zRotationAngle.addListener(getZRotationListener());

		rotateXAngleField.textProperty().addListener(getRotateXAngleFieldListener());
		rotateYAngleField.textProperty().addListener(getRotateYAngleFieldListener());
		rotateZAngleField.textProperty().addListener(getRotateZAngleFieldListener());

		// set initial values
		xRotationSlider.setValue(xRotationAngle.get());
		yRotationSlider.setValue(yRotationAngle.get());
		zRotationSlider.setValue(zRotationAngle.get());
		
		rotateXAngleField.setText(this.xRotationAngle.toString());
		rotateYAngleField.setText(this.yRotationAngle.toString());
		rotateZAngleField.setText(this.zRotationAngle.toString());

		rotateXAngleField.textProperty().bindBidirectional(xRotationAngle, new NumberStringConverter());
		rotateYAngleField.textProperty().bindBidirectional(yRotationAngle, new NumberStringConverter());
		rotateZAngleField.textProperty().bindBidirectional(zRotationAngle, new NumberStringConverter());

		xRotationSlider.valueProperty().addListener(getXRotationSliderListener());
		yRotationSlider.valueProperty().addListener(getYRotationSliderListener());
		zRotationSlider.valueProperty().addListener(getZRotationSliderListener());

	}

	private ChangeListener<Number> getXRotationListener() {
		return new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				xRotationSlider.setValue(xRotationAngle.get());
			}
		};
	}

	private ChangeListener<Number> getYRotationListener() {
		return new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				yRotationSlider.setValue(yRotationAngle.get());
			}
		};
	}

	private ChangeListener<Number> getZRotationListener() {
		return new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				zRotationSlider.setValue(zRotationAngle.get());
			}
		};
	}

	public ChangeListener<Number> getXRotationSliderListener() {
		return new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				xRotationAngle.set(xRotationSlider.getValue());
			}
		};
	}

	public ChangeListener<Number> getYRotationSliderListener() {
		return new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				yRotationAngle.set(yRotationSlider.getValue());
			}
		};
	}

	public ChangeListener<Number> getZRotationSliderListener() {
		return new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				zRotationAngle.set(zRotationSlider.getValue());
			}
		};
	}

	/*
	 * WHY CAN"T THESE BE HANDLED WITH CATCH PARSEEXCEPTION ??
	 */
	private ChangeListener<String> getRotateXAngleFieldListener() {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.isEmpty()) {
					// check if initialization
					if (newValue.startsWith(DoublePropStr)) return;
					
					
					try {
						double rotateXAngleVal = Double.parseDouble(newValue);
								
						if (rotateXAngleVal > -360. && rotateXAngleVal < 360.) {
							xRotationAngle.set(rotateXAngleVal);
						}
					} catch (Exception e) {
						xRotationAngle.set(ZERO);
//						 e.printStackTrace();
					}
				}
			}
		};
	}

	private ChangeListener<String> getRotateYAngleFieldListener() {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.isEmpty()) {
					// check if initialization
					if (newValue.startsWith(DoublePropStr)) return;
					
					
					try {
						double rotateYAngleVal = Double.parseDouble(newValue);

						if (rotateYAngleVal > -360. && rotateYAngleVal < 360.) {
							yRotationAngle.set(rotateYAngleVal);
						}
					} catch (Exception e) {
						yRotationAngle.set(ZERO);
						// e.printStackTrace();
					}
				}
			}
		};
	}

	private ChangeListener<String> getRotateZAngleFieldListener() {
		return new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.isEmpty()) {
					// check if initialization
					if (newValue.startsWith(DoublePropStr)) return;
					
					try {
						double rotateZAngleVal = Double.parseDouble(newValue);

						if (rotateZAngleVal > -360. && rotateZAngleVal < 360.) {
							zRotationAngle.set(rotateZAngleVal);
						}
					} catch (Exception e) {
						zRotationAngle.set(ZERO);
						// e.printStackTrace();
					}
				}
			}
		};
	}

	private void assertFXMLNodes() {
		assert(xRotationSlider != null);
		assert(yRotationSlider != null);
		assert(zRotationSlider != null);
	}
	
	private final static double ZERO = 0.;
	private final static String DoublePropStr = "DoubleProperty [value: ";
}
