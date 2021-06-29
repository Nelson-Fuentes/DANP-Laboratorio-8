package com.unsa.epis.danp.laboratorio8;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.unsa.epis.danp.laboratorio8.databinding.FragmentFirstBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class FirstFragment extends Fragment {

    private SensorManager sensorManager;

    private Sensor sensor_accelerometer_lineal;
    private SensorEventListener sensor_event_listener_accelerometer_lineal;
    private DecimalFormat decimalFormat;
    private FragmentFirstBinding binding;

    private TextView x_sensor;
    private TextView y_sensor;
    private TextView z_sensor;

    private GraphView graph;
    private GraphView graph2;


    private float index = 0;

    private int max_points = 1000;


    private LineGraphSeries <DataPoint> x_serie;
    private LineGraphSeries <DataPoint> y_serie;
    private LineGraphSeries <DataPoint> z_serie;

    private LineGraphSeries <DataPoint> accelertion_serie;


    private TextView acceleration_total_textview;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String pattern = "0.######";
        this.decimalFormat = new DecimalFormat(pattern);

        this.sensorManager = (SensorManager) this.getContext().getSystemService(Context.SENSOR_SERVICE);
        this.sensor_accelerometer_lineal = this.sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        this.x_sensor = (TextView) this.getView().findViewById(R.id.x_sensor);
        this.y_sensor = (TextView) this.getView().findViewById(R.id.y_sensor);
        this.z_sensor = (TextView) this.getView().findViewById(R.id.z_sensor);

        this.acceleration_total_textview = (TextView) this.getView().findViewById(R.id.acceleration_total_textview);
        this.graph = (GraphView) this.getView().findViewById(R.id.graph);
        graph.setVisibility(View.VISIBLE);

        this.graph2 = (GraphView) this.getView().findViewById(R.id.graph_2);
        graph2.setVisibility(View.VISIBLE);


        this.x_serie = new LineGraphSeries<DataPoint>(new DataPoint[]{});
        this.y_serie = new LineGraphSeries<DataPoint>(new DataPoint[]{});
        this.z_serie = new LineGraphSeries<DataPoint>(new DataPoint[]{});
        this.accelertion_serie = new LineGraphSeries<DataPoint>(new DataPoint[]{});

        this.x_serie.setColor(Color.RED);
        this.y_serie.setColor(Color.GREEN);
        this.accelertion_serie.setColor(Color.MAGENTA);

        this.graph.addSeries(x_serie);
        this.graph.addSeries(y_serie);
        this.graph.addSeries(z_serie);

        this.graph2.addSeries(this.accelertion_serie);
        if (this.sensor_accelerometer_lineal ==null){
            Toast.makeText(this.getContext(), "Dispositivo no cuenta con sensor", Toast.LENGTH_LONG);
        } else {
            this.sensor_event_listener_accelerometer_lineal = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    FirstFragment.this.get_data(event.values[0], event.values[1], event.values[2]);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
            sensorManager.registerListener(
                    this.sensor_event_listener_accelerometer_lineal,
                    this.sensor_accelerometer_lineal,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }

    }

    protected void get_data(float x, float y, float z){
        this.x_sensor.setText(this.decimalFormat.format(x));
        this.y_sensor.setText(this.decimalFormat.format(y));
        this.z_sensor.setText(this.decimalFormat.format(z));
        double acceleration_total = Math.sqrt( x*x + y*y + z*z  );
        this.x_serie.appendData(new DataPoint(this.index, x), false, this.max_points);
        this.y_serie.appendData(new DataPoint(this.index, y), false, this.max_points);
        this.z_serie.appendData(new DataPoint(this.index, z), false, this.max_points);
        this.acceleration_total_textview.setText(this.decimalFormat.format(acceleration_total));
        this.accelertion_serie.appendData(new DataPoint(this.index, acceleration_total), false, this.max_points);
        this.graph.getViewport().setMaxX(this.index);
        this.graph.getViewport().setMinX(Math.max(this.index - max_points, 0));
        graph.getViewport().setXAxisBoundsManual(true);

        this.graph2.getViewport().setMaxX(this.index);
        this.graph2.getViewport().setMinX(Math.max(this.index - max_points, 0));
        graph2.getViewport().setXAxisBoundsManual(true);

        this.index++;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}