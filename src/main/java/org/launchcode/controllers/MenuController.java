package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;
    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model){
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String add(Model model) {

        model.addAttribute("title", "Menu");
        model.addAttribute("menu", new Menu());
        return "menu/add";

    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(Model model,
                      @ModelAttribute @Valid Menu menu, Errors errors) {

        if (errors.hasErrors()) {
            return "menu/add";
        }
        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int id) {

        Menu menu = menuDao.findOne(id);

        model.addAttribute("title", menu.getName());
        model.addAttribute("menu", menu);
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int id) {

        Menu menu = menuDao.findOne(id);

        AddMenuItemForm itemForm = new AddMenuItemForm(menu, cheeseDao.findAll());

        model.addAttribute("title", "Add item to menu: " + menu.getName());
        model.addAttribute("form", itemForm);
        return "menu/add-item";

    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.POST)
    public String addItem(Model model,
                      @ModelAttribute @Valid AddMenuItemForm form, Errors errors, @PathVariable int id) {

         if (errors.hasErrors()) {
          model.addAttribute("title", "Add Item");
            return "menu/add-item";
        }

        Menu menu = menuDao.findOne(form.getMenuId());
        Cheese cheese = cheeseDao.findOne(form.getCheeseId());
        model.addAttribute("form", form);


        menu.addItem(cheese);
        menuDao.save(menu);

        return "redirect:../view/" + menu.getId();
    }
}
