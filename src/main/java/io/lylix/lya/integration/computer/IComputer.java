package io.lylix.lya.integration.computer;

public interface IComputer
{
    String getName();

    String[] getMethods();

    Object[] invoke(int method, Object[] args) throws Exception;
}
