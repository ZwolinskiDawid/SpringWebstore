package com.packt.webstore.controller;

import com.packt.webstore.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.packt.webstore.service.ProductService;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
	
    @RequestMapping
    public String list(Model model) {
        model.addAttribute("products", productService.getAllProducts());

        return "products";
    }
    
    @RequestMapping("/all")
    public String allProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        
        return "products";
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String getAddNewProductForm(Model model) {
        Product newProduct = new Product();
        model.addAttribute("newProduct", newProduct);
        return "addProduct";
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddNewProductForm(@ModelAttribute("newProduct") Product newProduct,
            BindingResult result) {
        
        String[] suppressedFields = result.getSuppressedFields();
        if(suppressedFields.length > 0) {
            throw new RuntimeException("Próba wiązania niedozwolonych pól: " + 
                    StringUtils.arrayToCommaDelimitedString(suppressedFields));
        }
        
        productService.addProduct(newProduct);
        return "redirect:/products";
    }
    
    @RequestMapping("/product")
    public String getProductById(@RequestParam String id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "product";
    }
    
    @RequestMapping("/{category}")
    public String getProductsByCategory(Model model, 
            @PathVariable("category") String productCategory) {
        
        model.addAttribute("products", productService.getProductsByCategory(productCategory));
        return "products";        
    }
    
    @RequestMapping("/{category}/{byPrice}")
    public String getProductsByFilter(@PathVariable String category, @MatrixVariable(pathVar = "byPrice")
            Map<String, List<String>> filterParams, @RequestParam String manufacturer,
            Model model) {
        
        model.addAttribute("products", productService.getProductsByFilter(filterParams, category, manufacturer));
        return "products";
    }
    
    @InitBinder
    public void initialiseBinder(WebDataBinder binder) {
        binder.setDisallowedFields("unitsInOrder", "discontinued");
    }
}