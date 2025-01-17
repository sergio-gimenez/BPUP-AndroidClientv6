/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities.patientdashboard.vitals;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.formdisplay.FormDisplayActivity;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardFragment;
import org.openmrs.mobile.application.OpenMRSInflater;
import org.openmrs.mobile.bundle.FormFieldsWrapper;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterMethods;
import org.openmrs.mobile.models.Form;
import org.openmrs.mobile.models.ObservationMethods;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ToastUtil;

public class PatientStaticEncounterFragment extends PatientDashboardFragment implements PatientDashboardContract.ViewStaticEncounter {

    private LinearLayout mContent;
    private LinearLayout mFormHeader;
    private TextView mEmptyList;
    private TextView mLastVitalsDate;
    private TextView mLabel;
    private LayoutInflater mInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setPresenter(mPresenter);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_patient_vitals, null, false);
        mContent = (LinearLayout) root.findViewById(R.id.vitalsDetailsContent);
        mEmptyList = (TextView) root.findViewById(R.id.lastVitalsNoneLabel);
        mLastVitalsDate = (TextView) root.findViewById(R.id.lastVitalsDate);
        mFormHeader = (LinearLayout) root.findViewById(R.id.lastVitalsLayout);
        mLabel = (TextView) root.findViewById(R.id.lastVitalsLabel);
        ImageButton formEditIcon = (ImageButton) root.findViewById(R.id.form_edit_icon);

        formEditIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PatientStaticEncounterPresenter) mPresenter).startFormDisplayActivityWithEncounter();
            }
        });

        this.mInflater = inflater;

        FontsUtil.setFont(mEmptyList, FontsUtil.OpenFonts.OPEN_SANS_EXTRA_BOLD);
        FontsUtil.setFont(mLabel, FontsUtil.OpenFonts.OPEN_SANS_EXTRA_BOLD);
        FontsUtil.setFont(mLastVitalsDate, FontsUtil.OpenFonts.OPEN_SANS_SEMIBOLD);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // This method is intentionally empty
    }

    @Override
    public void showNoEncountersNotification() {
        mFormHeader.setVisibility(View.GONE);
        mContent.setVisibility(View.GONE);
        mEmptyList.setVisibility(View.VISIBLE);
        mEmptyList.setText(getString(R.string.last_vitals_none_label));
    }

    @Override
    public void showStaticEncounter(EncounterMethods encounter) {
        mLastVitalsDate.setText(DateUtils.convertTime(encounter.getEncounterDatetime(), DateUtils.DATE_WITH_TIME_FORMAT));
        mLabel.setText(encounter.getFormName());
        OpenMRSInflater openMRSInflater = new OpenMRSInflater(mInflater);
        mContent.removeAllViews();
        for (ObservationMethods obs : encounter.getObservationsMethods()) {
            openMRSInflater.addKeyValueStringView(mContent, obs.getDisplay(), obs.getDisplayValue());
        }
    }

    /*@Override
    public void showStaticEncounter(Encounter encounter) {
        mLastVitalsDate.setText(DateUtils.convertTime(encounter.getEncounterDatetime(), DateUtils.DATE_WITH_TIME_FORMAT));
        mLabel.setText(encounter.getDisplay());
        OpenMRSInflater openMRSInflater = new OpenMRSInflater(mInflater);
        mContent.removeAllViews();
        for (Observation obs : encounter.getObservations()) {
            openMRSInflater.addKeyValueStringView(mContent, obs.getDisplay(), obs.getDisplayValue());
        }
    }*/

    @Override
    public void startFormDisplayActivity(Encounter encounter) {
        Form form = encounter.getForm();
        if(form != null){
            Intent intent = new Intent(getContext(), FormDisplayActivity.class);
            intent.putExtra(ApplicationConstants.BundleKeys.FORM_NAME, form.getName());
            intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, encounter.getPatient().getId());
            intent.putExtra(ApplicationConstants.BundleKeys.VALUEREFERENCE, form.getValueReference());
            intent.putExtra(ApplicationConstants.BundleKeys.ENCOUNTERTYPE, encounter.getEncounterType().getUuid());
            intent.putExtra(ApplicationConstants.BundleKeys.ENCOUNTER_SYNC, false);
            intent.putParcelableArrayListExtra(ApplicationConstants.BundleKeys.FORM_FIELDS_LIST_BUNDLE, FormFieldsWrapper.create(encounter));
            startActivity(intent);
        } else {
            ToastUtil.notify("This form is not supported");
        }

    }

    @Override
    public void showErrorToast(String errorMessage) {
        ToastUtil.error(errorMessage);
    }

    public static PatientStaticEncounterFragment newInstance() {
        return new PatientStaticEncounterFragment();
    }
}