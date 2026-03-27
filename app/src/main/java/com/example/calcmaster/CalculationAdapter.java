package com.example.calcmaster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CalculationAdapter extends RecyclerView.Adapter<CalculationAdapter.ViewHolder> {

    private List<Calculation> calculations;

    public CalculationAdapter(List<Calculation> calculations) {
        this.calculations = calculations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_calculation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Calculation calculation = calculations.get(position);
        holder.inputEditText.setText(calculation.getInput()); // Set input expression in EditText
        holder.outputEditText.setText(calculation.getResult()); // Set result in EditText

        // Disable editing for the EditText fields
        holder.inputEditText.setFocusable(false);
        holder.inputEditText.setFocusableInTouchMode(false);
        holder.outputEditText.setFocusable(false);
        holder.outputEditText.setFocusableInTouchMode(false);


    }

    @Override
    public int getItemCount() {
        return calculations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView inputEditText; // Changed from TextView to EditText
        public TextView outputEditText; // Changed from TextView to EditText
        public TextView lineView;

        public ViewHolder(View itemView) {
            super(itemView);
            inputEditText = itemView.findViewById(R.id.get_input); // Ensure ID matches XML
            outputEditText = itemView.findViewById(R.id.get_output); // Ensure ID matches XML
            lineView = itemView.findViewById(R.id.lineView);
        }
    }
}