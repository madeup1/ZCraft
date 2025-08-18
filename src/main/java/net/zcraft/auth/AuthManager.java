package net.zcraft.auth;

import lombok.Getter;
import lombok.Setter;
import net.zcraft.network.ZCraftConnection;

public class AuthManager
{
    @Getter @Setter
    private IAuthProvider provider;


    public boolean isAuthenticated(ZCraftConnection connection)
    {
        if (this.provider == null)
            return true;
        return this.provider.isAuthenticated(connection);
    }
}
