package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Collection;

abstract class FilmorateController<T> {

    @GetMapping
    public abstract Collection<T> getAll();

    @PostMapping
    public abstract T create(T t);

    @PutMapping
    public abstract T update(T t);
}