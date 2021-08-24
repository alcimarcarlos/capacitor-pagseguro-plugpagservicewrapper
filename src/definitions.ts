declare module '@capacitor/core' {
  interface PluginRegistry {
    PlugPagServiceWrapper: PlugPagServiceWrapperPlugin;
  }
}

export interface PlugPagServiceWrapperPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  getLibVersion(): Promise <{results: any[]}>;
  reprintStablishmentReceipt(): Promise <{results: any[]}>;
  reprintCustomerReceipt(): Promise <{results: any[]}>;
  getLastApprovedTransaction(): Promise <{results: any[]}>;
  createPayment(request : {
    reference: string,
    type:string, 
    installments:string,
    amount: string
  }): Promise <{results: any[]}>;
}
