package com.example.zakatcalculator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    EditText goldWeightEditText, goldValueEditText;
    Spinner goldTypeSpinner;
    TextView resultTextView;
    Button calculateButton;
    String selectedGoldType;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize UI elements
        goldWeightEditText = findViewById(R.id.goldWeight);
        goldValueEditText = findViewById(R.id.goldValue);
        goldTypeSpinner = findViewById(R.id.goldTypeSpinner);
        resultTextView = findViewById(R.id.resultText);
        calculateButton = findViewById(R.id.calculateButton);

        // Set up the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gold_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goldTypeSpinner.setAdapter(adapter);

        // Handle spinner selection
        goldTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGoldType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGoldType = "Keep"; // Default to "Keep" if nothing is selected
            }
        });

        // Add error handling for button click
        calculateButton.setOnClickListener(v -> {
            try {
                // Validate input fields
                String weightInput = goldWeightEditText.getText().toString();
                String valueInput = goldValueEditText.getText().toString();

                if (weightInput.isEmpty() || valueInput.isEmpty()) {
                    showError("Please fill in all fields.");
                    return;
                }

                double weight = Double.parseDouble(weightInput);
                double valuePerGram = Double.parseDouble(valueInput);

                if (weight <= 0 || valuePerGram <= 0) {
                    showError("Gold weight and value must be positive numbers.");
                    return;
                }

                int uruf = selectedGoldType.equals("Keep") ? 85 : 200; // Uruf for Keep (85) and Wear (200)

                // Calculate Zakat
                double totalValue = weight * valuePerGram; // Total Value of Gold
                double remainingWeight = weight - uruf;   // Gold weight minus uruf
                double zakatPayable = remainingWeight > 0 ? remainingWeight * valuePerGram : 0;
                double totalZakat = zakatPayable * 0.025; // 2.5% of zakat payable

                // Display Results
                String result = "Total Value of Gold: RM " + String.format("%.2f", totalValue) + "\n"
                        + "Gold Weight Minus Uruf: " + (remainingWeight > 0 ? String.format("%.2f", remainingWeight) : "0") + " grams\n"
                        + "Zakat Payable: RM " + String.format("%.2f", zakatPayable) + "\n"
                        + "Total Zakat: RM " + String.format("%.2f", totalZakat);

                resultTextView.setText(result);

            } catch (NumberFormatException e) {
                // Handle invalid number formats
                showError("Please enter valid numerical inputs.");
            } catch (Exception e) {
                // Catch all other exceptions
                showError("An unexpected error occurred. Please try again.");
                e.printStackTrace(); // Log the exception for debugging (optional)
            }
        });
    }

    private void showError(String errorMessage) {
        // Display error message as a Toast and reset the resultTextView
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        resultTextView.setText(""); // Clear result area
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            // Navigate to About Us page
            Intent intent = new Intent(this, AboutUsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_share) {
            // Share the app link
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this Zakat Calculator app!");
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
