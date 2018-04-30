package de.koudingspawn.vault.kubernetes;

import de.koudingspawn.vault.crd.DoneableVault;
import de.koudingspawn.vault.crd.Vault;
import de.koudingspawn.vault.crd.VaultList;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.api.model.apiextensions.DoneableCustomResourceDefinition;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class KubernetesConnection {

    @Bean
    @Profile("development")
    public KubernetesClient testClient() {
        Config config = new ConfigBuilder().withMasterUrl("http://localhost:8080").withWatchReconnectLimit(5).build();
        return new DefaultKubernetesClient(config);
    }

    @Bean
    @Profile("!development")
    public KubernetesClient client() {
        return new DefaultKubernetesClient();
    }

    @Bean
    public MixedOperation<Vault, VaultList, DoneableVault, Resource<Vault, DoneableVault>> customResource(
            KubernetesClient client, @Value("${kubernetes.crd.name}") String crdName) {
        Resource<CustomResourceDefinition, DoneableCustomResourceDefinition> crdResource
                = client.customResourceDefinitions().withName(crdName);
        return client.customResources(crdResource.get(), Vault.class, VaultList.class, DoneableVault.class);
    }

}
