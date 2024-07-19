package com.example.sbt_final_hr.app;

import com.example.sbt_final_hr.domain.model.dto.ProductRequest;
import com.example.sbt_final_hr.domain.model.entity.Product;
import com.example.sbt_final_hr.domain.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class ProductController {


    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    //create 화면
    @GetMapping("/createProduct")
    public String getProduct(Model model) {
        model.addAttribute("productRequest", new ProductRequest());
        return "createProduct";

    }

    @PostMapping("/createProduct")
    public String createProduct(Model model, @ModelAttribute("productRequest") ProductRequest productRequest) {
        productService.saveProduct(productRequest);
        return "redirect:/productList";

    }

    @GetMapping("/productList")
    public String getProductList(Model model) {
        List<Product> products = productService.findAll();
        //값 뿌려주기
        model.addAttribute("products", products);
        //과자삭제용으로 PK 값받기용
        model.addAttribute("productRequest", new ProductRequest());
        return "productOutput.html";
    }

    @PostMapping("/productList")
    public String deleteProductList(Model model, @ModelAttribute("productRequest") ProductRequest productRequest) {
        productService.deleteById(productRequest);
        return "redirect:/productList";
    }

    @GetMapping("/productUpdate")
    public String getProductUpdate(Model model,  @RequestParam Map<String,Object> params) {
        //주소통해 파라미터 보내주기용
        Long id = Long.parseLong(params.get("id").toString());
        Product product = productService.findById(id);
        //UPDATE화면에서 값들 받아서 보내주기용
        model.addAttribute("productRequest", new ProductRequest());
        //DB에서 PK로 검색한 그 엔티티 가져와서 UPDATE화면에 보여주기용 
        model.addAttribute("product", product);
        return "productUpdate.html";
    }

    @PostMapping("/productUpdate")
    public String updateProduct(Model model, @ModelAttribute("productRequest") ProductRequest productRequest) {
        productService.saveProduct(productRequest);
        return "redirect:/productList";
    }


}
