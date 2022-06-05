import bos.api.View;

import java.util.ArrayList;

class MyFirstCustomCheck {

  public void main(String[] args, String name) {
    View view = new View<>();
    for (int i = 0; i < 10; i++) {
      System.out.println(view.updateView()); // Noncompliant
    }

    for (int i = 0; i < 10; i++) {
      getView().updateView(); // Noncompliant
    }

    ArrayList<Object> list = new ArrayList<>();
    list.forEach(item -> {
      view.updateView(); // Noncompliant
    });

    list.forEach(item -> {
      getView().updateView(); // Noncompliant
    });

    if (true) {
      view.updateView();
    }

    view.updateView();
  }
}
