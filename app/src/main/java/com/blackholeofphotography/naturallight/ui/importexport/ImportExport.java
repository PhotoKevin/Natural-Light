package com.blackholeofphotography.naturallight.ui.importexport;

import org.json.JSONArray;
import com.github.erosb.jsonsKema.*;

public class ImportExport
{
   public boolean validate(String jsonData, String jsonSchema) throws Exception
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
