package com.ihuntech.poshakwaale.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ihuntech.poshakwaale.Adapter.CategoryAdapter;
import com.ihuntech.poshakwaale.Adapter.ProductAdapter;
import com.ihuntech.poshakwaale.Common.Common;
import com.ihuntech.poshakwaale.Interface.CategoryClickListener;
import com.ihuntech.poshakwaale.Model.Category;
import com.ihuntech.poshakwaale.Model.Product;
import com.ihuntech.poshakwaale.R;
import com.ihuntech.poshakwaale.Remote.IMyAPI;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class HomeFragment extends Fragment implements CategoryClickListener {

    RecyclerView recyclerCategory, recyclerProduct;
    TextView txtName, txtCategory;

    EditText edtProduct;

    CircleImageView imgProfile;

    List<Category> categoryList = new ArrayList<>();

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    IMyAPI iMyAPI;

    List<Product> localDataSource = new ArrayList<>();

    ProductAdapter searchAdapter, adapter;

    ProgressDialog dialog;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dialog = new ProgressDialog(getContext());

        iMyAPI = Common.getAPI();

        recyclerCategory = view.findViewById(R.id.recyclerCategory);
        recyclerProduct = view.findViewById(R.id.recyclerProduct);

        txtName = view.findViewById(R.id.txtName);
        txtCategory = view.findViewById(R.id.txtCategory);

        edtProduct = view.findViewById(R.id.edtProduct);
        imgProfile = view.findViewById(R.id.imgProfile);

        txtName.setSelected(true);

        recyclerCategory.setNestedScrollingEnabled(false);
        recyclerCategory.setHasFixedSize(true);
        recyclerCategory.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        recyclerProduct.setNestedScrollingEnabled(false);
        recyclerProduct.setHasFixedSize(true);
//        recyclerProduct.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerProduct.setLayoutManager(new GridLayoutManager(getContext(),2));

        loadUserDetails();
        loadCategory();
        loadAllProducts();

        edtProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                startSearch(text);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void loadUserDetails() {
        txtName.setText("\t\t"+"Poshaak User");
        Picasso
                .get()
                .load(R.drawable.profile)
                .error(R.drawable.profile)
                .into(imgProfile);
    }

    private void startSearch(CharSequence text) {
        List<Product> result = new ArrayList<>();

        String name = text.toString();

        for (Product state : localDataSource)   {
            if (state.name.toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT)))    {
                result.add(state);
            }
        }
        searchAdapter = new ProductAdapter(getContext(), result);
        recyclerProduct.setAdapter(searchAdapter);
    }


    private void loadAllProducts() {
        dialog.setTitle("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        compositeDisposable.add(
                iMyAPI.getAllItems()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Product>>() {
                                       @Override
                                       public void accept(List<Product> products) throws Exception {
                                           localDataSource = products;

                                           adapter = new ProductAdapter(getContext(),products);

                                           if (adapter.getItemCount() == 0) {
                                               recyclerProduct.setVisibility(View.GONE);
                                           }
                                           else {
                                               recyclerProduct.setVisibility(View.VISIBLE);
                                               recyclerProduct.setAdapter(adapter);
                                           }
                                           dialog.dismiss();

                                       }
                                   }
                        )
        );
    }

    private void loadCategory() {
        dialog.setTitle("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        compositeDisposable.add(
                iMyAPI.getCategory()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Category>>() {
                                       @Override
                                       public void accept(List<Category> categories) throws Exception {

                                           categoryList.clear();

                                           categoryList = categories;

                                           CategoryAdapter adapter = new CategoryAdapter(
                                                   getContext(),
                                                   categories,
                                                   HomeFragment.this
                                           );

                                           recyclerCategory.setAdapter(adapter);
                                           dialog.dismiss();
                                       }
                                   }
                        )
        );
    }

    @Override
    public void onCategoryClick(int pos) {
        String id = categoryList.get(pos).id.trim();
        String name = categoryList.get(pos).name.trim();

        if (name != null)   {
            txtCategory.setVisibility(View.VISIBLE);
            txtCategory.setPaintFlags(txtCategory.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            txtCategory.setText(name);
        }

        loadProducts(id);
    }

    private void loadProducts(String id) {
        dialog.setTitle("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        compositeDisposable.add(
                iMyAPI.getItemByCategory(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Product>>() {
                                       @Override
                                       public void accept(List<Product> products) throws Exception {

                                           localDataSource = products;

                                           adapter = new ProductAdapter(getContext(),products);

                                           if (adapter.getItemCount() == 0) {
                                               recyclerProduct.setVisibility(View.GONE);
//                                               imgComing.setVisibility(View.VISIBLE);
                                           }
                                           else {
//                                               imgComing.setVisibility(View.GONE);
                                               recyclerProduct.setVisibility(View.VISIBLE);
                                               recyclerProduct.setAdapter(adapter);
                                           }

                                           dialog.dismiss();

                                       }
                                   }
                        )
        );
    }


    @Override
    public void onResume() {
        super.onResume();
        loadUserDetails();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        compositeDisposable.dispose();
        super.onDestroy();
    }
}