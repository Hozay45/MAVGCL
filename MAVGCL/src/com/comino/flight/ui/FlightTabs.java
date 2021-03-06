/****************************************************************************
 *
 *   Copyright (c) 2017 Eike Mansfeld ecm@gmx.de. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 ****************************************************************************/

package com.comino.flight.ui;

import java.util.ArrayList;
import java.util.List;

import com.comino.flight.observables.StateProperties;
import com.comino.flight.ui.panel.control.FlightControlPanel;
import com.comino.flight.ui.sidebar.DetailsWidget;
import com.comino.flight.ui.sidebar.MSPCtlWidget;
import com.comino.flight.ui.sidebar.TuningWidget;
import com.comino.flight.ui.tabs.FlightXYAnalysisTab;
import com.comino.flight.ui.tabs.FlightXtAnalysisTab;
import com.comino.flight.ui.tabs.MAVInspectorTab;
import com.comino.flight.ui.tabs.MAVOpenMapTab;
import com.comino.flight.ui.tabs.MavLinkShellTab;
import com.comino.flight.ui.widgets.camera.CameraWidget;
import com.comino.flight.ui.widgets.statusline.StatusLineWidget;
import com.comino.mav.control.IMAVController;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class FlightTabs extends Pane {

	@FXML
	private TabPane tabpane;

	@FXML
	private FlightXtAnalysisTab xtanalysistab;

	@FXML
	private FlightXYAnalysisTab xyanalysistab;

	@FXML
	private MAVInspectorTab mavinspectortab;

	@FXML
	private MAVOpenMapTab mavmaptab;

	@FXML
	private HBox sidebar;

	@FXML
	private DetailsWidget details;

	@FXML
	private TuningWidget tuning;

	@FXML
	private MSPCtlWidget mspctl;


	@FXML
	private CameraWidget camera;

//	@FXML
//	private MAVParameterTab mavparametertab;

	@FXML
	private MavLinkShellTab mavlinkshelltab;

	private List<Pane> tabs = new ArrayList<Pane>();


	@FXML
	private void initialize() {
		tabs.add(xtanalysistab);
		tabs.add(xyanalysistab);
		tabs.add(mavmaptab);
		tabs.add(mavinspectortab);
		tabs.add(mavlinkshelltab);

	}

	public void activateCurrentTab(boolean disable) {
		if(!disable) {
			int tab = tabpane.getSelectionModel().getSelectedIndex();
			tabs.get(tab).setDisable(false);
		}
	}

	public void setup(FlightControlPanel flightControl, StatusLineWidget statusline, IMAVController control) {

		tabpane.prefHeightProperty().bind(heightProperty());
		tabpane.prefWidthProperty().bind(widthProperty());

		xtanalysistab.setDisable(true);
		xyanalysistab.setDisable(true);
		mavinspectortab.setDisable(true);
		mavmaptab.setDisable(true);
		mavlinkshelltab.setDisable(true);


		if(camera!=null) {
			camera.setup(control);
			camera.fadeProperty().bind(flightControl.getControl().getVideoVisibility());
		}

		details.fadeProperty().bind(flightControl.getControl().getDetailVisibility());
		details.setup(control);

		tuning.fadeProperty().bind(flightControl.getControl().getTuningVisibility());
		tuning.setup(control);

		mspctl.fadeProperty().bind(flightControl.getControl().getVehicleCtlVisibility());
		mspctl.setup(control);

//		experimental.fadeProperty().bind(flightControl.getControl().getExperimentalVisibility());
//		experimental.setup(control);

		mavmaptab.setup(flightControl.getChartControl(),control);
		mavinspectortab.setup(control);
		xtanalysistab.setup(flightControl.getChartControl(),control);
		xyanalysistab.setup(flightControl.getChartControl(),control);

		mavlinkshelltab.setup(control);

		this.tabpane.getTabs().get(3).setDisable(true);
		this.tabpane.getTabs().get(4).setDisable(true);


		StateProperties.getInstance().getConnectedProperty().addListener((observable, oldvalue, newvalue) -> {
			this.tabpane.getTabs().get(3).setDisable(!newvalue.booleanValue());
			this.tabpane.getTabs().get(4).setDisable(!newvalue.booleanValue() || control.isSimulation());
			flightControl.getControl().getDetailVisibility().setValue(newvalue.booleanValue());

		});

		HBox.setHgrow(tabpane, Priority.ALWAYS);

		tuning.managedProperty().bind(tuning.visibleProperty());
		details.managedProperty().bind(details.visibleProperty());
		mspctl.managedProperty().bind(mspctl.visibleProperty());

		tabpane.getSelectionModel().selectedIndexProperty().addListener((obs,ov,nv)->{
			for(int i =0; i<tabs.size();i++)
				tabs.get(i).setDisable(i!=nv.intValue());
		});

		xtanalysistab.setDisable(false);
		control.getCollector().clearModelList();
	}

}
