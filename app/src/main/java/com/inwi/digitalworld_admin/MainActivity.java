package com.inwi.digitalworld_admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.inwi.digitalworld_admin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rev_orders;
    private RecyclerView.Adapter myAdapter;

    private Activity myActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myActivity = this;
        rev_orders= findViewById(R.id.rev_orders);
        rev_orders.setLayoutManager(new LinearLayoutManager(this));



        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("TAG", "Listen failed.", e);
                            return;
                        }

                        JSONArray orders = new JSONArray();
                        for (QueryDocumentSnapshot document : value) {
                            String name = document.getData().get("name").toString();
                            String category = document.getData().get("category").toString();
                            String quantity = document.getData().get("quantity").toString();
                            String user = document.getData().get("user").toString();

                            JSONObject order = new JSONObject();
                            try {
                                order.put("code", document.getId());
                                order.put("name", name);
                                order.put("category", category);
                                order.put("quantity", quantity);
                                order.put("user", user);

                                orders.put(order);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }

                        myAdapter = new OrdersAdapter(orders, myActivity);

                        rev_orders.setAdapter(myAdapter);
                    }
                });

    }
}

class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private JSONArray orders;
    private Activity myActivity;

    public OrdersAdapter(JSONArray orders, Activity myActivity) {
        this.orders = orders;
        this.myActivity = myActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_order, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        try {
            Log.e("POSITION", "POS: " + position);
            String name = orders.getJSONObject(position).getString("name");
            String category = orders.getJSONObject(position).getString("category");
            String quantity = orders.getJSONObject(position).getString("quantity");
            String user = orders.getJSONObject(position).getString("user");

            holder.tev_name.setText(name);
            holder.tev_category.setText(category);
            holder.tev_quantity.setText(quantity);
            holder.tev_user.setText(user);


        } catch (JSONException e) {
        }

    }

    @Override
    public int getItemCount() {
        Log.e("QUANTITY_ORDERS", "" + this.orders.length());
        return this.orders.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tev_name;
        private TextView tev_category;
        private TextView tev_quantity;
        private TextView tev_user;
        public ViewHolder(View v) {
            super(v);
            tev_name = v.findViewById(R.id.tev_name);
            tev_category = v.findViewById(R.id.tev_category);
            tev_quantity = v.findViewById(R.id.tev_quantity);
            tev_user= v.findViewById(R.id.tev_user);

        }
    }
}