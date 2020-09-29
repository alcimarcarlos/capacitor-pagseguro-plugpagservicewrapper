declare module '@capacitor/core' {
  interface PluginRegistry {
    PlugPagServiceWrapper: PlugPagServiceWrapperPlugin;
  }
}

export interface PlugPagServiceWrapperPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  getContacts(filter: string): Promise <{results: any[]}>;
  getLibVersion(filter: string): Promise <{results: any[]}>;
  reprintStablishmentReceipt(filter: string): Promise <{results: any[]}>;
  reprintCustomerReceipt(filter: string): Promise <{results: any[]}>;
  getLastApprovedTransaction(filter: string): Promise <{results: any[]}>;
  startPayment(filter: string): Promise <{results: any[]}>;

}
