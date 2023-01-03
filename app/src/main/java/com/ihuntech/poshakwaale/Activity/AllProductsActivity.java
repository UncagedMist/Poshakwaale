package com.ihuntech.poshakwaale.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import com.ihuntech.poshakwaale.Adapter.CategoryAdapter;
import com.ihuntech.poshakwaale.Adapter.ProductAdapter;
import com.ihuntech.poshakwaale.Common.Common;
import com.ihuntech.poshakwaale.Fragments.HomeFragment;
import com.ihuntech.poshakwaale.Interface.CategoryClickListener;
import com.ihuntech.poshakwaale.Model.Category;
import com.ihuntech.poshakwaale.Model.Product;
import com.ihuntech.poshakwaale.R;
import com.ihuntech.poshakwaale.Remote.IMyAPI;

import java.util.ArrayList;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AllProductsActivity extends AppCompatActivity implements CategoryClickListener {

    RecyclerView recyclerCategory, recyclerProduct;

    ProgressDialog dialog;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    IMyAPI iMyAPI;

    List<Category> categoryList = new ArrayList<>();

    NoInternetDialog noInternetDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        noInternetDialog = new NoInternetDialog.Builder(this).build();

        dialog = new ProgressDialog(this);

        iMyAPI = Common.getAPI();

        recyclerCategory = findViewById(R.id.recyclerCategory);
        recyclerProduct = findViewById(R.id.recyclerProduct);

        recyclerCategory.setNestedScrollingEnabled(false);
        recyclerCategory.setHasFixedSize(true);
        recyclerCategory.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        recyclerProduct.setNestedScrollingEnabled(false);
        recyclerProduct.setHasFixedSize(true);
//        recyclerProduct.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerProduct.setLayoutManager(new GridLayoutManager(this,2));

        loadCategory();
        loadAllProducts();
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

                                           ProductAdapter adapter = new ProductAdapter(AllProductsActivity.this,products);

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
                                                   AllProductsActivity.this,
                                                   categories,
                                                   AllProductsActivity.this
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

//        if (name != null)   {
//            txtCategory.setVisibility(View.VISIBLE);
//            txtCategory.setPaintFlags(txtCategory.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//            txtCategory.setText(name);
//        }

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


                                           ProductAdapter adapter = new ProductAdapter(AllProductsActivity.this,products);

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
    protected void onDestroy() {
        compositeDisposable.clear();
        compositeDisposable.dispose();
        super.onDestroy();
        noInternetDialog.onDestroy();
    }
}