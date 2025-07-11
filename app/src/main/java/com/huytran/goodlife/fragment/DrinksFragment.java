package com.huytran.goodlife.fragment;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huytran.goodlife.R;
import com.huytran.goodlife.adapter.ViewAdapter;
import com.huytran.goodlife.model.Item;
import com.huytran.goodlife.pages.dietary.DietaryActivity;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DrinksFragment extends Fragment {
    private static final String TAG = "ExcelRead";
    private final List<Item> items = new ArrayList<>();
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ViewAdapter viewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_drinks, container, false);

        return view;

    }

    public void loadData() {
        if (items.isEmpty()) {
            new Thread(() -> {
                readExcelFile();

                requireActivity().runOnUiThread(() -> {
                    viewAdapter.notifyDataSetChanged();

                    if (getActivity() instanceof DietaryActivity) {
                        ((DietaryActivity) getActivity()).hideLoading();
                    }
                });
            }).start();
        }
    }

    public void readExcelFile() {
        String path = "Diary.xlsx";

        try {

            AssetManager am = getContext().getAssets();
            InputStream fileInputStream = am.open(path);

            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheetAt(2);

            int imageNameIndex = 4000;

            for (int rowIndex = 0; rowIndex < sheet.getPhysicalNumberOfRows() - 1; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                Cell cell = row.getCell(1);
                String name = cell.getStringCellValue();
                cell = row.getCell(2);
                int kcal = (int) cell.getNumericCellValue();
                cell = row.getCell(3);
                double protein = cell.getNumericCellValue();
                cell = row.getCell(4);
                double lipid = cell.getNumericCellValue();
                cell = row.getCell(5);
                double glucid = cell.getNumericCellValue();
                cell = row.getCell(6);
                int unit = (int) cell.getNumericCellValue();

                if (imageNameIndex <= 4125) {
                    imageNameIndex++;
                }

                String i_name = "n" + imageNameIndex;

                String unit_type;
                if (unit == 0) {
                    unit_type = "(g)";
                } else {
                    unit_type = "(ml)";
                }

                if (this.getResources().getIdentifier(i_name, "drawable", getActivity().getPackageName()) == 0) {
                    items.add(new Item(String.valueOf(name), R.drawable.noimageavailable, kcal, protein, lipid, glucid, unit_type));
                } else {
                    items.add(new Item(String.valueOf(name), this.getResources().getIdentifier(i_name, "drawable", getActivity().getPackageName()), kcal, protein, lipid, glucid, unit_type));
                }
            }
            fileInputStream.close();

            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycleView);
        searchView = view.findViewById(R.id.search_bar);
        viewAdapter = new ViewAdapter(getContext(), items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(viewAdapter);

        if (getActivity() instanceof DietaryActivity) {
            ((DietaryActivity) getActivity()).showLoading();
        }

        loadData();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                findData(newText);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void findData(String name) {
        List<Item> list = new ArrayList<>();
        for (Item data : items) {
            if (data.getName().toLowerCase().contains(name.toLowerCase())) {
                list.add(data);
            }
        }
        viewAdapter.updateList(list);
    }
}