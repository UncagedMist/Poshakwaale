package com.ihuntech.poshakwaale.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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

import com.ihuntech.poshakwaale.Activity.AllProductsActivity;
import com.ihuntech.poshakwaale.Activity.CartActivity;
import com.ihuntech.poshakwaale.Adapter.CategoryAdapter;
import com.ihuntech.poshakwaale.Adapter.ProductAdapter;
import com.ihuntech.poshakwaale.Common.Common;
import com.ihuntech.poshakwaale.Interface.CategoryClickListener;
import com.ihuntech.poshakwaale.Model.Banner;
import com.ihuntech.poshakwaale.Model.Category;
import com.ihuntech.poshakwaale.Model.Product;
import com.ihuntech.poshakwaale.R;
import com.ihuntech.poshakwaale.Remote.IMyAPI;
import com.squareup.picasso.Picasso;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import am.appwise.components.ni.NoInternetDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class HomeFragment extends Fragment {

    TextView txtName;

    EditText edtProduct;

    CircleImageView imgProfile;

    List<Product> localDataSource = new ArrayList<>();

    ProductAdapter searchAdapter;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    IMyAPI iMyAPI;

    NoInternetDialog noInternetDialog;

    RecyclerView recyclerSearch;

    ImageCarousel carousel;

    ProgressDialog dialog;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        noInternetDialog = new NoInternetDialog.Builder(this).build();

        dialog = new ProgressDialog(getContext());

        iMyAPI = Common.getAPI();

        txtName = view.findViewById(R.id.txtName);
        carousel = view.findViewById(R.id.carousel);

        edtProduct = view.findViewById(R.id.edtProduct);
        imgProfile = view.findViewById(R.id.imgProfile);

        recyclerSearch = view.findViewById(R.id.recyclerSearch);

        carousel.registerLifecycle(getLifecycle());

        txtName.setSelected(true);

        recyclerSearch.setNestedScrollingEnabled(false);
        recyclerSearch.setHasFixedSize(true);
        recyclerSearch.setLayoutManager(new LinearLayoutManager(getContext()));

        loadBanner();
        loadUserDetails();
        loadAllProducts();

        onClicked(view);

        edtProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                startSearch(text);

                if (text.toString().length() == 0)  {
                    loadBanner();
                    recyclerSearch.setVisibility(View.GONE);
                }
                else {
                    recyclerSearch.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return view;
    }

    private void loadBanner() {
        dialog.setTitle("कृपया प्रतीक्षा करें...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        compositeDisposable.add(
                iMyAPI.getBanner()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Banner>>() {
                                       @Override
                                       public void accept(List<Banner> featuredList) throws Exception {
                                           ArrayList<CarouselItem> carouselItems = new ArrayList<>();

                                           carouselItems.clear();

                                           for (int i = 0; i < featuredList.size(); i++) {
                                               carouselItems.add(new CarouselItem(
                                                       new StringBuilder(Common.IMAGE_URL)
                                                               .append(featuredList.get(i).photo).toString(),
                                                       Common.stripHtml(featuredList.get(i).title))
                                               );
                                           }
                                           carousel.setData(carouselItems);
                                           dialog.dismiss();
                                       }
                                   }
                        )
        );
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
        recyclerSearch.setAdapter(searchAdapter);
    }

    private void loadAllProducts() {
        compositeDisposable.add(
                iMyAPI.getAllItems()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<Product>>() {
                                       @Override
                                       public void accept(List<Product> products) throws Exception {
                                           localDataSource = products;

                                       }
                                   }
                        )
        );
    }

    private void onClicked(View view) {
        view.findViewById(R.id.relMore1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),AllProductsActivity.class));
            }
        });

        view.findViewById(R.id.relMore2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),AllProductsActivity.class));
            }
        });

        view.findViewById(R.id.relMore3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),AllProductsActivity.class));
            }
        });

        view.findViewById(R.id.linALl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),AllProductsActivity.class));
            }
        });

        view.findViewById(R.id.linCart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), CartActivity.class));
            }
        });
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
        noInternetDialog.onDestroy();
    }
}