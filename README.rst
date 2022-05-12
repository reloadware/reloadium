



.. raw:: html

    <p align="center">
        <img src="media/logo.svg" width="400px" alt="Logo">
    </p>
    <h4 align="center"> The missing element of Python - Advanced Hot Reloading & Profiling </h4>

.. class:: center


.. raw:: html

    <p align="center">
        <img src="media/example_small.gif" width="900px" alt="Example">
    </p>






Details
#######


.. image:: https://badge.fury.io/py/reloadium.svg
    :target: https://badge.fury.io/py/reloadium

.. image:: https://img.shields.io/jetbrains/plugin/v/18509-reloadium
    :target: https://plugins.jetbrains.com/plugin/18509-reloadium

.. image:: https://img.shields.io/jetbrains/plugin/d/18509-reloadium
    :target: https://plugins.jetbrains.com/plugin/18509-reloadium

.. image:: https://img.shields.io/jetbrains/plugin/r/stars/18509-reloadium
    :target: https://plugins.jetbrains.com/plugin/18509-reloadium




| Reloadium adds hot reloading and profiling features to any Python application


Installing
##########

| If you are a PyCharm user please check out `Reloadium plugin <https://plugins.jetbrains.com/plugin/18509-reloadium>`_
| Plugins for other IDEs are coming soon.

| Reloadium can be also used as a standalone library by installing it manually with pip:

.. code-block:: console

   pip install reloadium


Using
#####

Running python files:


.. code-block:: console

   reloadium run example.py

Running modules:

.. code-block:: console

   reloadium run -m my_module


| To apply your changes simply save a file


General Python Features
#######################

| Reloadium will rerun current function when changed.
| Developers get instant feedback whether the code is working or not.

.. figure:: media/frame_reloading.gif
    :width: 700
    :alt: Frame Reloading


| Reloadium will handle errors during reloading function and let you fix mistakes.

.. figure:: media/fixing_errors.gif
    :width: 700
    :alt: Frame Reloading


| Reloadium is capable of reloading files across the whole project.
| Tweaking with settings could not be easier.

.. figure:: media/multi_file_reloading.gif
    :width: 700
    :alt: Frame Reloading


Django
######

| Reloadium will hot reload views and refresh page on save.

.. figure:: media/django_page_refreshing.gif
    :width: 700
    :alt: Page Refreshing


| Reloadium will rollback database changes to prevent creating unwanted objects after reloading current function.

.. figure:: media/django_rolling_back.gif
    :width: 700
    :alt: Rolling back db


Flask
#####

| Reloadium will hot reload flask apps and refresh page on save.
| Changing content can be seen in real time making web development much smoother.

.. figure:: media/flask.gif
    :width: 700
    :alt: Flask



SqlAlchemy
##########

| Reloadium will rollback database changes to prevent creating unwanted objects after reloading current function.

.. figure:: media/sqlalchemy.gif
    :width: 700
    :alt: SqlAlchemy


Pandas
##########

| Reloadium hot reloads pandas objects.
| Manipulating complex dataframes for data science could not be easier.

.. figure:: media/pandas.gif
    :width: 700
    :alt: Pandas