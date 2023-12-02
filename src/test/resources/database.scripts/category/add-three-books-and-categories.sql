insert into books (id, title, author, isbn, price)
values (1, "test-book1", "test-author", "123-123-0001", 100.99);
insert into books (id, title, author, isbn, price)
values (2, "test-book2", "test-author", "123-123-0002", 200.99);
insert into books (id, title, author, isbn, price)
values (3, "test-book3", "test-author", "123-123-0003", 300.99);

insert into categories (id, name)
values (1, "Test category 1");
insert into categories (id, name)
values (2, "Test category 2");
insert into categories (id, name)
values (3, "Test category 3");

insert into books_categories (book_id, category_id)
values (1, 1);
insert into books_categories (book_id, category_id)
values (1, 2);
insert into books_categories (book_id, category_id)
values (2, 2);
insert into books_categories (book_id, category_id)
values (2, 3);
insert into books_categories (book_id, category_id)
values (3, 1);
