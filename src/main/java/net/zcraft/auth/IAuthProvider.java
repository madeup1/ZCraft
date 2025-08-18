package net.zcraft.auth;

import net.zcraft.network.ZCraftConnection;

public interface IAuthProvider
{
    boolean isAuthenticated(ZCraftConnection connection);
}
