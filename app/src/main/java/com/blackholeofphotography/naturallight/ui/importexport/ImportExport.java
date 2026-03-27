package com.blackholeofphotography.naturallight.ui.importexport;

import com.github.erosb.jsonsKema.*;

@SuppressWarnings("unused")
public class ImportExport
{
   @SuppressWarnings("SameReturnValue")
   public boolean validate(String jsonData, String jsonSchema)
   {
      Schema schema = new SchemaLoader(jsonSchema).load();

      // create a validator instance for each validation (one-time use object)
      Validator validator = Validator.create(schema, new ValidatorConfig(FormatValidationPolicy.ALWAYS));
      JsonValue instance = new JsonParser(jsonData).parse();
      ValidationFailure failure = validator.validate(instance);

      System.out.println(failure);
      return true;
   }
}
