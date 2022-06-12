import bos.api.View;

import java.util.ArrayList;

class MyFirstCustomCheck {

  public void main(String[] args, String name) {
    View view = new View<>();

    for (int i = 0; i < 10; i++) {
      System.out.println(view.updateView()); // Noncompliant
      getView().updateView(); // Noncompliant
      view.updateView(); // Noncompliant
    }


    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        getView().updateView(); // Noncompliant
      }


      for (Object item: list) {
        view.updateView(); // Noncompliant
      }
    }
    list.forEach(item -> {
      view.updateView(); // Noncompliant
    });

    for (Object item: list) {
      view.updateView(); // Noncompliant
    }

    ArrayList<Object> list = new ArrayList<>();

    if (true) {
      view.updateView();
    }

    view.updateView();
  }
}
