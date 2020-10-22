package com.idemia.pocidemiacarabineros.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.idemia.pocidemiacarabineros.Modelo.ControlVehiculo;
import com.idemia.pocidemiacarabineros.Modelo.ControlVehiculoContract;
import com.idemia.pocidemiacarabineros.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AdapterInfoPatente extends BaseAdapter {
    protected ArrayList<ControlVehiculo> items;
    private Context context;
    ViewHolder holder;
    // override other abstract methods here
    public AdapterInfoPatente(Activity activity, ArrayList<ControlVehiculo> items){
        this.items = items;
        this.context = activity.getBaseContext();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)  context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.info_patentes, null);
            holder = new ViewHolder();
            holder.patente = (TextView) convertView.findViewById(R.id.tx_patente);
            holder.direccion = (TextView) convertView.findViewById(R.id.tx_direccion);
            holder.fecha = (TextView) convertView.findViewById(R.id.tx_fecha);
            holder.hora = (TextView) convertView.findViewById(R.id.tx_hora);
            convertView.setTag(holder);
            ControlVehiculo item = items.get(position);
            holder.patente.setText(item.getPatente());
            holder.direccion.setText(item.getDireccion());
            holder.fecha.setText(item.getFecha());
            holder.hora.setText(item.getHora());
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }
    static class ViewHolder{
        TextView patente;
        TextView direccion;
        TextView fecha;
        TextView hora;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
